pipeline {
  agent any
  stages {
    stage('Build and test') {
      steps{
	    wrap([$class: 'Xvfb', additionalOptions: '-displayfd', displayNameOffset: 1]) {
		  withMaven {
            sh 'mvn clean install -f invesdwin-context-parent/pom.xml -T4'
          }
	    }
      }
    }
  }
}