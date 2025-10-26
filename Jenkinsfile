pipeline {
    agent any
    
    parameters {
        choice(
           name: 'PROFILE',
           defaultValue: 'testdata,prod,batch,es'
        )
    }
    tools {
        jdk 'openjdk-21-jdk'
    }

    environment {
        COMPOSE_FILE = 'docker-compose.yml'
        SPRING_PROFILES_ACTIVE = "${params.PROFILE}"

        //Build kit
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
                //
                success { sh 'echo "✅ Successfully Cloned Repository"' }
                failure { sh 'echo "❌ Failed to Clone Repository"' }
            }
        }

        stage('Replace Properties'){
                      steps{
                          script{
                              withCredentials([
                              file(credentialsId: 'SECRET_FILE2', variable: 'secretFile'),
                              file(credentialsId: 'SECRET_DOCKER_ENV',variable: 'envFile')
                              ]){
                                 sh '''
                                    cp $secretFile ./src/main/resources/application.yml
                                    cp $envFile .env
                                    '''
                              }
                          }
                      }
                }

        stage('Preparation'){
            parallel{
                stage('Check and Free Up Ports') {
                            steps {
                                sh """
                                for port in 5431 27016; do
                                    if lsof -i :\$port; then
                                        echo "Port \$port is in use. Killing the process..."
                                        sudo kill -9 \$(lsof -ti :\$port) || true
                                    fi
                                done
                                echo "Port cleanup complete."
                                """
                            }
                        }

                 stage('Remove Existing Docker Containers') {
                             steps {
                                 sh '''
                                 JENKINS_CONTAINER=$(docker ps -aqf "name=jenkins")
                                 ALL_CONTAINERS=$(docker ps -aq)

                                 for CONTAINER in $ALL_CONTAINERS; do
                                     if [ "$CONTAINER" != "$JENKINS_CONTAINER" ]; then
                                         docker stop $CONTAINER || true
                                         docker rm -f $CONTAINER || true
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
                           # 폴더 없으면 생성
                           mkdir -p esdata es-logs

                           # Elasticsearch 기본 UID(1000:1000)에 맞춰 소유권 변경
                           chown -R 1000:1000 esdata es-logs || true

                           # 읽기/쓰기 권한 부여
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
                sh 'SPRING_PROFILES_ACTIVE=$SPRING_PROFILES_ACTIVE docker-compose up -d --build'
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
