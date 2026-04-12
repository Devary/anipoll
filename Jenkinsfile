pipeline {
    agent any

    tools {
        jdk 'graalvm17'
        maven 'Maven'
    }

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        APP_NAME = 'anipoll'
        APP_VERSION = '1.0-SNAPSHOT'
        CORE_DIR = 'core'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                sh 'git status --short || true'
            }
        }

        stage('Build Core') {
            steps {
                dir("${env.CORE_DIR}") {
                    sh 'mvn -B -ntp clean package -DskipTests'
                }
            }
        }

        stage('Test Core') {
            steps {
                dir("${env.CORE_DIR}") {
                    sh 'mvn -B -ntp test'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'core/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            when {
                branch 'master'
            }
            steps {
                sh '''
                    set -e
                    rm -rf target/package
                    mkdir -p target/package/apps-repo

                    JAR_PATH=$(find core/target -maxdepth 1 -type f -name '*.jar' ! -name '*-sources.jar' ! -name '*-javadoc.jar' ! -name '*-runner.jar' | head -n 1)

                    if [ -z "$JAR_PATH" ]; then
                      echo "No build jar found in core/target"
                      exit 1
                    fi

                    cp "$JAR_PATH" "target/package/apps-repo/${APP_NAME}.jar"
                    cd target/package
                    zip -r "../${APP_NAME}-${APP_VERSION}.zip" .
                '''
                archiveArtifacts artifacts: 'target/*.zip', fingerprint: true, onlyIfSuccessful: true
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully.'
        }
        failure {
            echo 'Pipeline failed. Check compile/test logs above.'
        }
    }
}
