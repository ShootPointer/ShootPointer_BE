pipeline {
    agent any

    parameters {
        choice(
            name: 'PROFILE',
            choices: [
                'test-real-data,prod,batch,es,test-highlight-data',
                'prod,batch',
                'dev',
                'test-real-data',
                'prod',
                'batch',
                'es',
                'test-real-data,prod,batch,es',
                'dev,batch,es'
            ]
        )
    }

    tools {
        jdk 'openjdk-21-jdk'
    }

    environment {
        COMPOSE_FILE = 'docker-compose.yml'
        SPRING_PROFILES_ACTIVE = "${params.PROFILE}"

        // BuildKit 설정
        DOCKER_BUILDKIT = '1'
        BUILDKIT_PROGRESS = 'plain'
    }

    stages {

        stage('Prepare') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/ShootPointer/ShootPointer_BE.git'
            }
            post {
                success { sh 'echo "✅ Successfully Cloned Repository"' }
                failure { sh 'echo "❌ Failed to Clone Repository"' }
            }
        }

        stage('Replace Properties') {
            steps {
                script {
                    withCredentials([
                        file(credentialsId: 'SECRET_FILE2', variable: 'secretFile'),
                        file(credentialsId: 'SECRET_DOCKER_ENV', variable: 'envFile')
                    ]) {
                        sh '''
                            cp $secretFile ./src/main/resources/application.yml
                            cp $envFile .env
                        '''
                    }
                }
            }
        }

        stage('Preparation') {
            parallel {

                stage('Check and Free Up Ports') {
                    steps {
                        sh '''
                            for port in 5431 27016; do
                                if lsof -i :$port; then
                                    echo "Port $port is in use. Killing the process..."
                                    sudo kill -9 $(lsof -ti :$port) || true
                                fi
                            done
                            echo "Port cleanup complete."
                        '''
                    }
                }

                stage('Remove Existing Docker Containers') {
                    steps {
                        sh '''
                            # Jenkins 컨테이너 제외
                            JENKINS_CONTAINER=$(docker ps -aqf "name=jenkins")

                            # Nginx 컨테이너 제외
                            NGINX_CONTAINER=$(docker ps -aqf "name=nginx")

                            # 전체 컨테이너 목록 가져오기
                            ALL_CONTAINERS=$(docker ps -aq)

                            for CONTAINER in $ALL_CONTAINERS; do
                                if [ "$CONTAINER" != "$JENKINS_CONTAINER" ] && [ "$CONTAINER" != "$NGINX_CONTAINER" ]; then
                                    echo "Removing container: $CONTAINER"
                                    docker stop $CONTAINER || true
                                    docker rm -f $CONTAINER || true
                                else
                                    echo "Skipping protected container: $CONTAINER"
                                fi
                            done

                            docker-compose down --remove-orphans || true
                        '''
                    }
                    post {
                        success { sh 'echo "✅ Successfully Removed Docker Containers"' }
                        failure { sh 'echo "❌ Failed to Remove Docker Containers"' }
                    }
                }
            }
        }

        stage('Fix Elasticsearch Volume Permissions') {
            steps {
                sh '''
                    echo "🔧 Fixing Elasticsearch volume permissions..."
                    mkdir -p /var/lib/jenkins/esdata /var/lib/jenkins/es-logs
                    chown -R 1000:1000 /var/lib/jenkins/esdata /var/lib/jenkins/es-logs || true
                    chmod -R 775 /var/lib/jenkins/esdata /var/lib/jenkins/es-logs || true
                    echo "✅ Elasticsearch data/log volume permissions fixed."
                '''
            }
        }


        stage('Build and Deploy with Docker Compose') {
            environment {
                JAVA_TOOL_OPTIONS = "-Dorg.jenkinsci.plugins.durabletask.BourneShellScript.HEARTBEAT_CHECK_INTERVAL=86400"
            }
            steps {
                sh 'SPRING_PROFILES_ACTIVE=$SPRING_PROFILES_ACTIVE docker-compose up -d --build'
            }
            post {
                success { sh 'echo "✅ Successfully Deployed with Docker Compose"' }
                failure { sh 'echo "❌ Failed to Deploy with Docker Compose"' }
            }
        }
    }
}
