pipeline {
  agent any
  stages {
    stage('checkout') {
      steps {
        git 'https://github.com/MaastrichtU-IDS/data2services-sparql-operations.git'
      }
    }
    stage('build') {
      steps {
        sh 'docker build -t data2services-sparql-operations .'
      }
    }
  }
}