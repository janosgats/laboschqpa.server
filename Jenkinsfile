#!groovy

pipeline {
    agent any

    stages {
        stage('Build docker image') {
            steps {
                echo 'Building docker image...'
                sh "docker build -f docker/Dockerfile-k8s_dev-travis_build ."
            }
        }
        stage('Deploy to GKE') {
            steps {
                echo 'Deploying to GKE...'
            }
        }
    }
}