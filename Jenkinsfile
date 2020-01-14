pipeline {
  agent any
  stages {
    stage('checkout') {
      steps {
        git 'https://github.com/MaastrichtU-IDS/d2s-sparql-operations.git'
      }
    }
    stage('build') {
      steps {
        sh 'docker build -t d2s-sparql-operations .'
      }
    }
  }
}