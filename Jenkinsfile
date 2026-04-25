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
        HARBOR_REGISTRY = '192.168.178.41:30002'
        RUNDECK_HOST = '192.168.178.41'
        RUNDECK_PORT = '4440'
        HARBOR_PROJECT = 'library'
        IMAGE_NAME = 'anipoll'
        IMAGE_TAG = ''
        HARBOR_PREFIX = "${HARBOR_REGISTRY}/${HARBOR_PROJECT}"
        FULL_IMAGE = ''
        LATEST_IMAGE = ''
        DEPLOYMENT_NAME = "${IMAGE_NAME}"
        CONTAINER_NAME = "${IMAGE_NAME}"
        RUNDECK_JOB_ID = "1b180a49-b61b-4733-877e-03f3ea9f6939"
        NAMESPACE = 'default'
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

        stage('Deploy to JFrog') {
            when {
                branch 'master'
            }
            steps {
                dir("${env.CORE_DIR}") {
                    sh 'mvn -B -ntp -Puse-jfrog deploy -DskipTests'
                }
            }
        }

stage('Prepare Dockerfile') {
         steps {
           writeFile file: 'Dockerfile', text: '''
   FROM alpine:3.20
   CMD ["sh", "-c", "echo hello from jenkins harbor test && sleep 3600"]
   '''
         }
       }

       stage('Debug Variables') {
         steps {
           sh '''
             echo "LOCAL_IMAGE=$LOCAL_IMAGE"
             echo "FULL_IMAGE=$FULL_IMAGE"
             echo "HARBOR_REGISTRY=$HARBOR_REGISTRY"
             echo "BUILD_NUMBER=$BUILD_NUMBER"
             echo "IMAGE_NAME=$IMAGE_NAME"
             echo "IMAGE_TAG=$IMAGE_TAG"
             echo "LOCAL_IMAGE=$LOCAL_IMAGE"
             echo "FULL_IMAGE=$FULL_IMAGE"
             echo "DEPLOYMENT_NAME=$IMAGE_NAME"
             echo "CONTAINER_NAME=$IMAGE_NAME"
           '''
         }
       }
       stage('Set Image Names') {
         steps {
           script {
             def pom = readFile("${env.CORE_DIR}/pom.xml")
             def matcher = pom =~ /<version>([^<]+)<\/version>/

             if (!matcher.find()) {
               error('Could not resolve Maven project version from core/pom.xml')
             }

             def resolvedVersion = matcher.group(1).trim()

             if (!resolvedVersion || resolvedVersion == 'null') {
               error("Could not resolve Maven project version. Got: '${resolvedVersion}'")
             }

             env.APP_VERSION = resolvedVersion
             env.IMAGE_TAG = resolvedVersion

             writeFile file: 'target/.image-vars', text: """IMAGE_TAG=${resolvedVersion}
LOCAL_IMAGE=${env.IMAGE_NAME}:${resolvedVersion}
FULL_IMAGE=${env.HARBOR_REGISTRY}/${env.HARBOR_PROJECT}/${env.IMAGE_NAME}:${resolvedVersion}
LATEST_IMAGE=${env.HARBOR_REGISTRY}/${env.HARBOR_PROJECT}/${env.IMAGE_NAME}:latest
IMAGE_PATH=${env.HARBOR_REGISTRY}/${env.HARBOR_PROJECT}/${env.IMAGE_NAME}
"""

             sh 'cat target/.image-vars'
           }
         }
       }


       stage('Build Image') {
         steps {
           sh '''
             set -euo pipefail
             . target/.image-vars
             docker build -t "$LOCAL_IMAGE" .
           '''
         }
       }

       stage('Login to Harbor') {
         steps {
           withCredentials([usernamePassword(
             credentialsId: 'harbor-creds',
             usernameVariable: 'HARBOR_USER',
             passwordVariable: 'HARBOR_PASS'
           )]) {
             sh '''
               echo "$HARBOR_PASS" | docker login "$HARBOR_REGISTRY" -u "$HARBOR_USER" --password-stdin
             '''
           }
         }
       }

       stage('Tag Image') {
         steps {
           sh '''
             set -euo pipefail
             . target/.image-vars
             docker tag "$LOCAL_IMAGE" "$FULL_IMAGE"
           '''
         }
       }

       stage('Push Image') {
         steps {
           sh '''
             set -euo pipefail
             . target/.image-vars

             if docker manifest inspect "$FULL_IMAGE" >/dev/null 2>&1; then
               echo "Image already exists in Harbor, skipping version push: $FULL_IMAGE"
             else
               docker push "$FULL_IMAGE"
             fi

             docker tag "$LOCAL_IMAGE" "$LATEST_IMAGE"
             docker push "$LATEST_IMAGE"
           '''
         }
       }
        stage('Trigger Rundeck Deploy') {
             steps {
               withCredentials([string(credentialsId: 'rundeck-api-token', variable: 'RUNDECK_TOKEN')])
                {
                 sh '''
                   set -euo pipefail

                   . target/.image-vars

                   echo "IMAGE_PATH=$IMAGE_PATH"
                   echo "IMAGE_TAG=latest"
                   echo "NAMESPACE="
                   echo "DEPLOYMENT_NAME=$DEPLOYMENT_NAME"
                   echo "CONTAINER_NAME=$CONTAINER_NAME"

                   curl -sS -X POST "${RUNDECK_HOST}:${RUNDECK_PORT}/api/46/job/${RUNDECK_JOB_ID}/run" \
                     -H "X-Rundeck-Auth-Token: $RUNDECK_TOKEN" \
                     -H "Content-Type: application/json" \
                     -d "{
                       \\"options\\": {
                         \\"workspace\\": \\"${WORKSPACE}\\",
                         \\"image\\": \\"${IMAGE_PATH}\\",
                         \\"tag\\": \\"latest\\",
                         \\"namespace\\": \\"${NAMESPACE}\\",
                         \\"deployment\\": \\"${DEPLOYMENT_NAME}\\",
                         \\"container\\": \\"${CONTAINER_NAME}\\"
                       }
                     }"
                 '''
               }
             }
           }
       }


    post {
        success {
            echo 'Pipeline completed successfully.'
            echo "Pushed image: ${FULL_IMAGE}"
        }
        failure {
            echo 'Pipeline failed. Check compile/test logs above.'
        }
        always {
         sh 'docker logout ${HARBOR_REGISTRY} || true'
       }
    }
}
