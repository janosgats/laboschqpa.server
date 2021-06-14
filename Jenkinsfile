#!groovy

String PROJECT_IMAGE_NAME = 'laboschqpa-server'

String BRANCH_NAME = env.BRANCH_NAME.replace('/', '-')
String SHORT_COMMIT_HASH = env.GIT_COMMIT.substring(0, 7)

String IMAGE_NAME_COMMIT
String IMAGE_NAME_BRANCH
String IMAGE_NAME_LATEST

withCredentials([usernamePassword(credentialsId: 'DOCKER_HUB_CREDS', usernameVariable: 'DOCKER_HUB_USER')]) {
    IMAGE_NAME_BRANCH = env.DOCKER_HUB_USERNAME + '/' + PROJECT_IMAGE_NAME + ':' + BRANCH_NAME
    IMAGE_NAME_COMMIT = env.DOCKER_HUB_USERNAME + '/' + PROJECT_IMAGE_NAME + ':' + SHORT_COMMIT_HASH
    IMAGE_NAME_LATEST = env.DOCKER_HUB_USERNAME + '/' + PROJECT_IMAGE_NAME + ':latest'
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

    stages {
        stage('Build and publish docker image') {
            when {
                expression {
                    return !params.SKIP_DOCKER_BUILD
                }
            }
            steps {
                echo 'Building docker image...'
                echo IMAGE_NAME_BRANCH
                echo IMAGE_NAME_COMMIT
                echo IMAGE_NAME_LATEST

                withCredentials([usernamePassword(credentialsId: 'DOCKER_HUB_CREDS', passwordVariable: 'DOCKER_HUB_PASSWORD', usernameVariable: 'DOCKER_HUB_USER')]) {
                    // some block
                }

                sh "docker build -f docker/Dockerfile-k8s_dev-travis_build ."
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