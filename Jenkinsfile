def bumpPatchVersion(String version) {
    def snapshot = version.endsWith('-SNAPSHOT')
    def base = snapshot ? version.replace('-SNAPSHOT', '') : version
    def parts = base.tokenize('.')

    if (parts.size() < 2) {
        error("Version must look like x.y or x.y.z[-SNAPSHOT]. Got: ${version}")
    }

    def numericParts = parts.collect { part ->
        if (!(part ==~ /\d+/)) {
            error("Version segment is not numeric in version: ${version}")
        }
        part as int
    }

    numericParts[-1] = numericParts[-1] + 1
    def bumped = numericParts.join('.')
    return snapshot ? "${bumped}-SNAPSHOT" : bumped
}

pipeline {
    agent any

    tools {
        jdk 'graalvm17'
        maven 'Maven'
    }

    parameters {
        string(name: 'MANUAL_VERSION', defaultValue: '', description: 'Optional: override the Maven version for this build')
    }

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        APP_NAME = 'anipoll'
        APP_VERSION = ''
        CORE_DIR = 'core'
        HARBOR_REGISTRY = '192.168.178.41:30002'
        RUNDECK_HOST = '192.168.178.41'
        RUNDECK_PORT = '4440'
        HARBOR_PROJECT = 'library'
        IMAGE_NAME = 'anipoll'
        IMAGE_TAG = ''
        PROJECT_TYPE = ''
        GRAALVM24_HOME = tool(name: 'graalvm24', type: 'hudson.model.JDK')
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

        stage('Resolve Version') {
            steps {
                script {
                    def manualVersion = params.MANUAL_VERSION?.trim()
                    def currentVersion = sh(
                        script: "mvn -B -ntp -q help:evaluate -Dexpression=project.version -DforceStdout",
                        returnStdout: true
                    ).trim()

                    if (!currentVersion || currentVersion == 'null') {
                        error('Could not resolve current Maven project version from pom.xml')
                    }

                    def targetVersion = manualVersion ? manualVersion : bumpPatchVersion(currentVersion)
                    env.APP_VERSION = targetVersion
                    env.IMAGE_TAG = targetVersion

                    sh """
                        mvn -B -ntp versions:set -DnewVersion=${targetVersion} -DprocessAllModules=true -DgenerateBackupPoms=false
                    """

                    sh 'git status --short'
                    writeFile file: 'target/.resolved-version', text: "${targetVersion}\n"
                    echo "Resolved Maven version: ${targetVersion}"
                }
            }
        }

        stage('Detect Project Type') {
            steps {
                script {
                    def pom = readFile("${env.CORE_DIR}/pom.xml")
                    def projectType = 'java'
                    if (pom.contains('quarkus-maven-plugin') || pom.contains('<artifactId>quarkus-bom</artifactId>')) {
                        projectType = 'quarkus'
                    } else if (pom.contains('spring-boot-maven-plugin') || pom.contains('org.springframework.boot')) {
                        projectType = 'spring-boot'
                    }

                    env.PROJECT_TYPE = projectType
                    writeFile file: 'target/.project-type', text: "${projectType}\n"
                    echo "PROJECT_TYPE=${projectType}"
                }
            }
        }

        stage('Build Core') {
            steps {
                dir("${env.CORE_DIR}") {
                    sh 'mvn -B -ntp clean package -DskipTests'
                }
            }
        }

        stage('Build Native Image') {
            steps {
                script {
                    def projectType = readFile('target/.project-type').trim()
                    if (projectType == 'quarkus') {
                        dir("${env.CORE_DIR}") {
                            withEnv(["JAVA_HOME=${env.GRAALVM24_HOME}", "PATH+GRAAL=${env.GRAALVM24_HOME}/bin"]) {
                                sh 'mvn -B -ntp package -DskipTests -Dnative'
                            }
                        }
                    } else {
                        echo "Skipping native image build for PROJECT_TYPE=${projectType}"
                    }
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
                script {
                    def projectType = readFile('target/.project-type').trim()
                    if (projectType == 'quarkus') {
                        sh '''
                            set -euo pipefail
                            rm -rf target/package
                            mkdir -p target/package/apps-repo

                            NATIVE_PATH=$(find core/target -maxdepth 1 -type f -perm -111 ! -name '*.jar' | head -n 1)

                            if [ -z "$NATIVE_PATH" ]; then
                              echo "No Quarkus native binary found in core/target"
                              exit 1
                            fi

                            cp "$NATIVE_PATH" "target/package/apps-repo/${APP_NAME}"
                            cd target/package
                            zip -r "../${APP_NAME}-${APP_VERSION}.zip" .
                        '''
                    } else if (projectType == 'spring-boot') {
                        sh '''
                            set -euo pipefail
                            rm -rf target/package
                            mkdir -p target/package/apps-repo

                            JAR_PATH=$(find core/target -maxdepth 1 -type f -name '*.jar' ! -name '*-sources.jar' ! -name '*-javadoc.jar' | head -n 1)

                            if [ -z "$JAR_PATH" ]; then
                              echo "No Spring Boot jar found in core/target"
                              exit 1
                            fi

                            cp "$JAR_PATH" "target/package/apps-repo/${APP_NAME}.jar"
                            cd target/package
                            zip -r "../${APP_NAME}-${APP_VERSION}.zip" .
                        '''
                    } else {
                        sh '''
                            set -euo pipefail
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
                    }
                }
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
             def resolvedVersion = sh(
               script: "mvn -B -ntp -q help:evaluate -Dexpression=project.version -DforceStdout",
               returnStdout: true
             ).trim()

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
            script {
                if (env.BRANCH_NAME == 'master') {
                    withCredentials([usernamePassword(credentialsId: 'github-creds', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS')]) {
                        sh '''
                          set -euo pipefail
                          git config user.name "jenkins"
                          git config user.email "jenkins@local"
                          git add pom.xml core/pom.xml service-template/pom.xml quarkus-service-template/pom.xml chassis/pom.xml 2>/dev/null || true
                          if ! git diff --cached --quiet; then
                            git commit -m "Bump Maven version to ${APP_VERSION} [skip ci]"
                            REMOTE_URL=$(git remote get-url origin)
                            AUTHED_URL=$(printf '%s' "$REMOTE_URL" | sed "s#https://#https://${GIT_USER}:${GIT_PASS}@#")
                            git push "$AUTHED_URL" HEAD:${BRANCH_NAME}
                          else
                            echo "No pom version changes to commit."
                          fi
                        '''
                    }
                } else {
                    echo "Skipping pom commit/push on branch ${env.BRANCH_NAME}"
                }
            }
            echo 'Pipeline completed successfully.'
            sh '''
              if [ -f target/.image-vars ]; then
                . target/.image-vars
                echo "Pushed image: $FULL_IMAGE"
                echo "Latest image: $LATEST_IMAGE"
              fi
            '''
        }
        failure {
            echo 'Pipeline failed. Check compile/test logs above.'
        }
        always {
         sh 'docker logout ${HARBOR_REGISTRY} || true'
       }
    }
}
