pipeline {
    agent any
    
    parameters {
        choice(
            name: 'PROFILE',
            choices: [
                'prod,batch',
                'test-real-data,prod,batch,es,test-highlight-data',
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

        // BuildKit for faster Docker builds
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
                            echo "🔐 Replacing application.yml and .env files..."
                            cp $secretFile ./src/main/resources/application.yml
                            cp $envFile .env
                            echo "✅ Secret files replaced successfully."
                        '''
                    }
                }
            }
        }

        stage('Preparation') {
            parallel {

                stage('Clean Up Used Ports & Containers') {
                    steps {
                        sh '''
                        echo "🧹 Cleaning up all non-Jenkins containers and freeing ports..."

                        # Jenkins 컨테이너 제외하고 모두 중지 및 삭제
                        JENKINS_CONTAINER=$(docker ps -aqf "name=jenkins")
                        ALL_CONTAINERS=$(docker ps -aq)

                        for CONTAINER in $ALL_CONTAINERS; do
                            if [ "$CONTAINER" != "$JENKINS_CONTAINER" ]; then
                                echo "Stopping and removing container: $CONTAINER"
                                docker stop $CONTAINER || true
                                docker rm -f $CONTAINER || true
                            fi
                        done

                        # 로컬 MongoDB 데몬이 켜져 있으면 중단
                        if pgrep mongod > /dev/null; then
                            echo "Stopping local MongoDB daemon..."
                            sudo systemctl stop mongod || true
                        fi

                        # 포트 충돌 방지용 주요 포트 해제
                        for port in 5432 5431 27017 27016 9200 6379 5601; do
                            PID=$(lsof -ti :$port || true)
                            if [ ! -z "$PID" ]; then
                                echo "Killing process on port $port (PID: $PID)"
                                sudo kill -9 $PID || true
                            fi
                        done

                        # docker-compose 잔여 컨테이너 정리
                        docker-compose down --remove-orphans || true

                        echo "✅ Port and container cleanup complete."
                        '''
                    }
                    post {
                        success { sh 'echo "✅ Cleanup completed successfully."' }
                        failure { sh 'echo "❌ Cleanup failed."' }
                    }
                }
            }
        }

        stage('Fix Elasticsearch Volume Permissions') {
            steps {
                sh '''
                    echo "🔧 Fixing Elasticsearch volume permissions..."
                    mkdir -p esdata es-logs
                    chown -R 1000:1000 esdata es-logs || true
                    chmod -R 775 esdata es-logs
                    echo "✅ Elasticsearch data/log volume permissions fixed."
                '''
            }
            post {
                success { sh 'echo "✅ Volume permissions fixed successfully."' }
                failure { sh 'echo "❌ Failed to fix volume permissions."' }
            }
        }

        stage('Build and Deploy with Docker Compose') {
            steps {
                sh '''
                echo "🚀 Starting deployment with profiles: $SPRING_PROFILES_ACTIVE"
                SPRING_PROFILES_ACTIVE=$SPRING_PROFILES_ACTIVE docker-compose up -d --build
                '''
            }
            environment {
                JAVA_TOOL_OPTIONS = "-Dorg.jenkinsci.plugins.durabletask.BourneShellScript.HEARTBEAT_CHECK_INTERVAL=86400"
            }
            post {
                success { sh 'echo "✅ Successfully Deployed with Docker Compose"' }
                failure { sh 'echo "❌ Failed to Deploy with Docker Compose"' }
            }
        }
    }
}
