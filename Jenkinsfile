
pipeline {
    agent any

    options {
        skipDefaultCheckout(true)
    }

    stages {
        stage('Checkout from GitHub') {
            steps {
                checkout scm
            }
        }

        stage('Check Tools') {
            steps {
                bat 'java -version'
                bat 'mvn -version'
                bat 'docker --version'
            }
        }

        stage('Build with Maven') {
            steps {
                bat 'mvn clean package -DskipTests'
            }
        }

       
         stage('Build Docker Image') {
           steps {
               bat 'set "DOCKER_HOST=npipe:////./pipe/dockerDesktopLinuxEngine" && docker info'
               bat 'set "DOCKER_HOST=npipe:////./pipe/dockerDesktopLinuxEngine" && docker build -t skillswap:1.0 .'
            }
        }

        }
    }
}
