pipeline {
  agent any
  stages {
    stage('checkout') {
      steps {
        git 'https://github.com/vemonet/rdf4j-sparql-operations.git'
      }
    }
    stage('build') {
      steps {
        sh 'docker build -t rdf4j-sparql-operations .'
      }
    }
  }
}