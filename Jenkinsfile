#!groovy

String PROJECT_IMAGE_NAME = 'laboschqpa-server'

String DOCKER_HUB_USERNAME
withCredentials([usernamePassword(credentialsId: 'DOCKER_HUB_CREDS', passwordVariable: 'DOCKER_HUB_PASSWORD', usernameVariable: 'DOCKER_HUB_USER')]) {
    DOCKER_HUB_USERNAME = "$DOCKER_HUB_USER"
}

def shouldDeployByDefault() {
    return env.BRANCH_NAME == 'master'
}

def shouldPublishAsLatest() {
    return env.BRANCH_NAME == 'master'
}

pipeline {
    agent any

    parameters {
        booleanParam(
                defaultValue: false,
                description: 'Skip building and publishing docker image',
                name: 'SKIP_DOCKER_BUILD'
        )
        booleanParam(
                defaultValue: false,
                description: 'Force deployment to GKE',
                name: 'FORCE_DEPLOY_TO_GKE'
        )
    }

    environment {
        SHORT_COMMIT_HASH = "${env.GIT_COMMIT.substring(0, 10)}"

        IMAGE_NAME_BRANCH = "${DOCKER_HUB_USERNAME}/${PROJECT_IMAGE_NAME}:${env.BRANCH_NAME.replace('/', '-')}"
        IMAGE_NAME_COMMIT = "${DOCKER_HUB_USERNAME}/${PROJECT_IMAGE_NAME}:${env.SHORT_COMMIT_HASH}"
        IMAGE_NAME_LATEST = "${DOCKER_HUB_USERNAME}/${PROJECT_IMAGE_NAME}:latest"
    }

    stages {
        stage('Build and publish docker image') {
            when {
                expression {
                    return !params.SKIP_DOCKER_BUILD
                }
            }
            steps {
                echo 'Logging in to docker'
                withCredentials([usernamePassword(credentialsId: 'DOCKER_HUB_CREDS', passwordVariable: 'DOCKER_HUB_PASSWORD', usernameVariable: 'DOCKER_HUB_USER')]) {
                    sh 'echo ${DOCKER_HUB_PASSWORD} | docker login -u ${DOCKER_HUB_USER} --password-stdin'
                }

                script {
                    String extraLatestTag = "";

                    if (shouldPublishAsLatest()) {
                        extraLatestTag = ' -t ${IMAGE_NAME_LATEST} '
                    }

                    echo 'Building docker image...'
                    sh 'docker build -t ${IMAGE_NAME_COMMIT} -t ${IMAGE_NAME_BRANCH} ' + extraLatestTag + ' -f docker/Dockerfile-k8s_dev-travis_build .'
                }

                script {
                    echo 'Publishing docker image...'
                    sh 'docker push ${IMAGE_NAME_BRANCH}'
                    sh 'docker push ${IMAGE_NAME_COMMIT}'
                    if (shouldPublishAsLatest()) {
                        sh 'docker push ${IMAGE_NAME_LATEST}'
                    }
                }
            }
        }

        stage('Deploy to GKE') {
            when {
                expression {
                    return shouldDeployByDefault() || params.FORCE_DEPLOY_TO_GKE
                }
            }
            steps {
                echo 'Deploying to GKE...'
            }
        }
    }

    post {
        cleanup {
            cleanWs()
        }
    }
}