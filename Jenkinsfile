def normalizeVersion(String version) {
    def snapshot = version.endsWith('-SNAPSHOT')
    def base = snapshot ? version.replace('-SNAPSHOT', '') : version
    def parts = base.tokenize('.')

    if (parts.isEmpty() || parts.size() > 3) {
        error("Version must look like x, x.y, or x.y.z[-SNAPSHOT]. Got: ${version}")
    }

    def numericParts = parts.collect { part ->
        if (!(part ==~ /\d+/)) {
            error("Version segment is not numeric in version: ${version}")
        }
        part as int
    }

    while (numericParts.size() < 3) {
        numericParts << 0
    }

    def normalized = numericParts.join('.')
    return snapshot ? "${normalized}-SNAPSHOT" : normalized
}

def bumpPatchVersion(String version) {
    def normalized = normalizeVersion(version)
    def snapshot = normalized.endsWith('-SNAPSHOT')
    def base = snapshot ? normalized.replace('-SNAPSHOT', '') : normalized
    def numericParts = base.tokenize('.').collect { it as int }

    numericParts[2] = numericParts[2] + 1

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
        booleanParam(name: 'GENERATE_NATIVE_IMAGE', defaultValue: false, description: 'Build the Quarkus native image for this run')
        booleanParam(name: 'PACKAGE_ONLY', defaultValue: false, description: 'Package/publish to Maven only and skip image build + deployment flow')
        booleanParam(name: 'SKIP_TESTS', defaultValue: false, description: 'Skip Maven test execution in build and test stages')
    }

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        APP_NAME = ''
        APP_VERSION = ''
        BUILD_DIR = '.'
        HARBOR_REGISTRY = '192.168.178.41:30002'
        RUNDECK_HOST = '192.168.178.41'
        RUNDECK_PORT = '4440'
        HARBOR_PROJECT = 'library'
        IMAGE_NAME = ''
        IMAGE_TAG = ''
        PROJECT_TYPE = ''
        GRAALVM24_HOME = tool(name: 'graalvm24', type: 'hudson.model.JDK')
        HARBOR_PREFIX = "${HARBOR_REGISTRY}/${HARBOR_PROJECT}"
        FULL_IMAGE = ''
        LATEST_IMAGE = ''
        DEPLOYMENT_NAME = ''
        CONTAINER_NAME = ''
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
                    sh 'mkdir -p target'
                    def manualVersion = params.MANUAL_VERSION?.trim()
                    def currentVersion = sh(
                        script: "mvn -B -ntp -q help:evaluate -Dexpression=project.version -DforceStdout",
                        returnStdout: true
                    ).trim()

                    if (!currentVersion || currentVersion == 'null') {
                        error('Could not resolve current Maven project version from pom.xml')
                    }

                    def effectiveCurrentVersion = normalizeVersion(currentVersion)
                    def targetVersion = effectiveCurrentVersion

                    if (env.BRANCH_NAME == 'master') {
                        targetVersion = manualVersion ? normalizeVersion(manualVersion) : bumpPatchVersion(currentVersion)

                        sh """
                            mvn -B -ntp versions:set -DnewVersion=${targetVersion} -DprocessAllModules=true -DgenerateBackupPoms=false
                        """

                        sh 'git status --short'
                    } else {
                        echo "Skipping version mutation on branch ${env.BRANCH_NAME}; using ${effectiveCurrentVersion}"
                    }

                    env.APP_VERSION = targetVersion
                    env.IMAGE_TAG = targetVersion
                    writeFile file: 'target/.resolved-version', text: "${targetVersion}\n"
                    echo "Resolved Maven version: ${targetVersion}"
                }
            }
        }

        stage('Resolve Project Layout') {
            steps {
                script {
                    sh 'mkdir -p target'
                    def rootPom = readFile('pom.xml')
                    def appName = sh(script: 'basename "$WORKSPACE"', returnStdout: true).trim()
                    def buildDir = '.'
                    def candidateModules = []

                    if (rootPom.contains('<modules>')) {
                        def matcher = rootPom =~ /<module>([^<]+)<\/module>/
                        matcher.each { candidateModules << it[1].trim() }
                    }

                    def moduleProjectType = { String pomText ->
                        if (pomText.contains('quarkus-maven-plugin') || pomText.contains('<artifactId>quarkus-bom</artifactId>')) {
                            return 'quarkus'
                        }
                        if (pomText.contains('spring-boot-maven-plugin') || pomText.contains('org.springframework.boot')) {
                            return 'spring-boot'
                        }
                        return 'java'
                    }

                    def rootProjectType = moduleProjectType(rootPom)
                    def projectType = rootProjectType

                    if (rootProjectType == 'java' && candidateModules) {
                        if (fileExists('core/pom.xml')) {
                            buildDir = 'core'
                        } else {
                            def matchedModule = candidateModules.find { module ->
                                if (!fileExists("${module}/pom.xml")) {
                                    return false
                                }
                                def modulePom = readFile("${module}/pom.xml")
                                def moduleType = moduleProjectType(modulePom)
                                if (moduleType != 'java') {
                                    projectType = moduleType
                                    return true
                                }
                                return false
                            }
                            if (matchedModule) {
                                buildDir = matchedModule
                            } else {
                                buildDir = candidateModules[0]
                            }
                        }

                        if (fileExists("${buildDir}/pom.xml")) {
                            projectType = moduleProjectType(readFile("${buildDir}/pom.xml"))
                        }
                    }

                    env.APP_NAME = appName
                    env.IMAGE_NAME = appName
                    env.DEPLOYMENT_NAME = appName
                    env.CONTAINER_NAME = appName
                    env.BUILD_DIR = buildDir
                    env.PROJECT_TYPE = projectType

                    writeFile file: 'target/.project-layout', text: "APP_NAME=${appName}\nBUILD_DIR=${buildDir}\nPROJECT_TYPE=${projectType}\n"
                    writeFile file: 'target/.project-type', text: "${projectType}\n"

                    echo "APP_NAME=${appName}"
                    echo "BUILD_DIR=${buildDir}"
                    echo "PROJECT_TYPE=${projectType}"
                }
            }
        }

        stage('Build Core') {
            steps {
                dir("${env.BUILD_DIR}") {
                    sh 'mvn -B -ntp clean package -DskipTests'
                }
            }
        }

        stage('Build Native Image') {
            when {
                expression { return params.GENERATE_NATIVE_IMAGE }
            }
            steps {
                script {
                    def projectType = env.PROJECT_TYPE?.trim()
                    if (projectType == 'quarkus') {
                        dir("${env.BUILD_DIR}") {
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
            when {
                expression { return !params.SKIP_TESTS }
            }
            steps {
                dir("${env.BUILD_DIR}") {
                    sh 'mvn -B -ntp test'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: "${env.BUILD_DIR}/target/surefire-reports/*.xml"
                }
            }
        }

        stage('Package') {
            when {
                branch 'master'
            }
            steps {
                script {
                    def projectType = env.PROJECT_TYPE?.trim()
                    def resolvedVersion = env.APP_VERSION?.trim()
                    if (!resolvedVersion) {
                        resolvedVersion = sh(
                            script: "mvn -B -ntp -q help:evaluate -Dexpression=project.version -DforceStdout",
                            returnStdout: true
                        ).trim()
                    }
                    env.APP_VERSION = resolvedVersion

                    if (projectType == 'quarkus' && params.GENERATE_NATIVE_IMAGE) {
                        sh """
                            set -euo pipefail
                            APP_VERSION='${resolvedVersion}'
                            rm -rf target/package
                            mkdir -p target/package/apps-repo

                            NATIVE_PATH=\$(find ${env.BUILD_DIR}/target -maxdepth 1 -type f -perm -111 ! -name '*.jar' | head -n 1)

                            if [ -z "\$NATIVE_PATH" ]; then
                              echo "No Quarkus native binary found in ${env.BUILD_DIR}/target"
                              exit 1
                            fi

                            cp "\$NATIVE_PATH" "target/package/apps-repo/${APP_NAME}"
                            cd target/package
                            zip -r "../${APP_NAME}-\${APP_VERSION}.zip" .
                        """
                    } else if (projectType == 'quarkus') {
                        sh """
                            set -euo pipefail
                            APP_VERSION='${resolvedVersion}'
                            rm -rf target/package
                            mkdir -p target/package/apps-repo

                            JAR_PATH=\$(find ${env.BUILD_DIR}/target -maxdepth 1 -type f -name '*.jar' ! -name '*-sources.jar' ! -name '*-javadoc.jar' ! -name '*-runner.jar' | head -n 1)

                            if [ -z "\$JAR_PATH" ]; then
                              echo "No Quarkus jar found in ${env.BUILD_DIR}/target"
                              exit 1
                            fi

                            cp "\$JAR_PATH" "target/package/apps-repo/${APP_NAME}.jar"
                            cd target/package
                            zip -r "../${APP_NAME}-\${APP_VERSION}.zip" .
                        """
                    } else if (projectType == 'spring-boot') {
                        sh """
                            set -euo pipefail
                            APP_VERSION='${resolvedVersion}'
                            rm -rf target/package
                            mkdir -p target/package/apps-repo

                            JAR_PATH=\$(find ${env.BUILD_DIR}/target -maxdepth 1 -type f -name '*.jar' ! -name '*-sources.jar' ! -name '*-javadoc.jar' | head -n 1)

                            if [ -z "\$JAR_PATH" ]; then
                              echo "No Spring Boot jar found in ${env.BUILD_DIR}/target"
                              exit 1
                            fi

                            cp "\$JAR_PATH" "target/package/apps-repo/${APP_NAME}.jar"
                            cd target/package
                            zip -r "../${APP_NAME}-\${APP_VERSION}.zip" .
                        """
                    } else {
                        sh """
                            set -euo pipefail
                            APP_VERSION='${resolvedVersion}'
                            rm -rf target/package
                            mkdir -p target/package/apps-repo

                            JAR_PATH=\$(find ${env.BUILD_DIR}/target -maxdepth 1 -type f -name '*.jar' ! -name '*-sources.jar' ! -name '*-javadoc.jar' ! -name '*-runner.jar' | head -n 1)

                            if [ -z "\$JAR_PATH" ]; then
                              echo "No build jar found in ${env.BUILD_DIR}/target"
                              exit 1
                            fi

                            cp "\$JAR_PATH" "target/package/apps-repo/${APP_NAME}.jar"
                            cd target/package
                            zip -r "../${APP_NAME}-\${APP_VERSION}.zip" .
                        """
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
                dir("${env.BUILD_DIR}") {
                    sh 'mvn -B -ntp -Puse-jfrog deploy -DskipTests'
                }
            }
        }

        stage('Prepare Dockerfile') {
            when {
                expression { return !params.PACKAGE_ONLY }
            }
            steps {
                writeFile file: 'Dockerfile', text: '''
FROM alpine:3.20
CMD ["sh", "-c", "echo hello from jenkins harbor test && sleep 3600"]
'''
            }
        }

        stage('Debug Variables') {
            when {
                expression { return !params.PACKAGE_ONLY }
            }
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
            when {
                expression { return !params.PACKAGE_ONLY }
            }
            steps {
                script {
                    sh 'mkdir -p target'
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
            when {
                expression { return !params.PACKAGE_ONLY }
            }
            steps {
                sh '''
                  set -euo pipefail
                  . target/.image-vars
                  docker build -t "$LOCAL_IMAGE" .
                '''
            }
        }

        stage('Login to Harbor') {
            when {
                expression { return !params.PACKAGE_ONLY }
            }
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
            when {
                expression { return !params.PACKAGE_ONLY }
            }
            steps {
                sh '''
                  set -euo pipefail
                  . target/.image-vars
                  docker tag "$LOCAL_IMAGE" "$FULL_IMAGE"
                '''
            }
        }

        stage('Push Image') {
            when {
                expression { return !params.PACKAGE_ONLY }
            }
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
            when {
                expression { return !params.PACKAGE_ONLY }
            }
            steps {
                withCredentials([string(credentialsId: 'rundeck-api-token', variable: 'RUNDECK_TOKEN')]) {
                    sh '''
                      set -euo pipefail

                      . target/.image-vars

                      echo "IMAGE_PATH=$IMAGE_PATH"
                      echo "IMAGE_TAG=$IMAGE_TAG"
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
                            \\"tag\\": \\"${IMAGE_TAG}\\",
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
                    sshagent(credentials: ['github-ssh']) {
                        sh '''
                          set -euxo pipefail
                          RESOLVED_VERSION="${APP_VERSION}"
                          git config user.name "jenkins"
                          git config user.email "jenkins@local"
                          git add pom.xml */pom.xml */*/pom.xml 2>/dev/null || true
                          if ! git diff --cached --quiet; then
                            git commit -m "Bump Maven version to ${RESOLVED_VERSION} [skip ci]"
                            REMOTE_URL=$(git remote get-url origin)
                            echo "Current origin: $REMOTE_URL"
                            if echo "$REMOTE_URL" | grep -q '^https://github.com/'; then
                              SSH_URL=$(printf '%s' "$REMOTE_URL" | sed -E 's#https://github.com/#git@github.com:#')
                              git remote set-url origin "$SSH_URL"
                              echo "Rewrote origin to SSH: $SSH_URL"
                            fi
                            git remote -v
                            git push origin HEAD:${BRANCH_NAME}
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
            script {
                if (params.PACKAGE_ONLY) {
                    echo 'PACKAGE_ONLY=true, so image build and deployment stages were skipped.'
                }
            }
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
