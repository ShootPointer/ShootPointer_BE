pipeline {
    agent any
    
    parameters {
        choice(
            name: 'PROFILE',
            choices: ['dev', 'testdata', 'prod','es,prod,test-real-data'],
            description: 'Select Spring Profile for deployment'
        )
    }
    tools {
        jdk 'openjdk-21-jdk'
    }

    environment {
        COMPOSE_FILE = 'docker-compose.yml'
        SPRING_PROFILES_ACTIVE = "${params.PROFILE}"
        JAVA_TOOL_OPTIONS = "-Dmanagement.metrics.enable.system=false"
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
                              withCredentials([file(credentialsId: 'SECRET_FILE2', variable: 'secretFile')]){
                                  sh 'cp $secretFile ./src/main/resources/application.yml'
                              }
                          }
                      }
                }

        stage('Build Gradle Test') {
            steps {
                sh 'echo "🔧 Build Gradle Test Start"'
                sh 'echo "JAVA_HOME is set to: $JAVA_HOME"'
                sh 'java -version'
                sh 'chmod +x gradlew'
                sh './gradlew clean build -x test --info'
            }
            post {
                success { sh 'echo "✅ Successfully Built Gradle Project"' }
                failure { sh 'echo "❌ Failed to Build Gradle Project"' }
            }
        }

        stage('Check and Free Up Ports') {
            steps {
                sh 'echo "🔌 Checking and Freeing Up Ports (6378, 5431, 27016)"'
                sh """
                for port in 6378 5431 27016 443; do
                    if lsof -i :\$port; then
                        echo "Port \$port is in use. Killing the process..."
                        sudo kill -9 \$(lsof -ti :\$port) || true
                    fi
                done
                echo "✅ Port cleanup complete."
                """
            }
        }

        stage('Remove Existing Docker Containers') {
            steps {
                sh 'echo "🛑 Stopping and Removing Existing Docker Containers except Jenkins"'
                sh '''
                JENKINS_CONTAINER=$(docker ps -aqf "name=jenkins")
                ALL_CONTAINERS=$(docker ps -aq)

                for CONTAINER in $ALL_CONTAINERS; do
                    if [ "$CONTAINER" != "$JENKINS_CONTAINER" ]; then
                        docker stop $CONTAINER || true
                        docker rm -f $CONTAINER || true
                    fi
                done

                docker-compose down --rmi all --remove-orphans || true
                '''
            }
            post {
                success { sh 'echo "✅ Successfully Removed Docker Containers"' }
                failure { sh 'echo "❌ Failed to Remove Docker Containers"' }
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
                sh 'echo "🚀 Building and Deploying Containers with Docker Compose"'
                sh 'docker system prune -a -f'
                sh 'SPRING_PROFILES_ACTIVE=$SPRING_PROFILES_ACTIVE docker-compose up -d --build'
            }
            post {
                success { sh 'echo "✅ Successfully Deployed with Docker Compose"' }
                failure { sh 'echo "❌ Failed to Deploy with Docker Compose"' }
            }
        }
    }
}
