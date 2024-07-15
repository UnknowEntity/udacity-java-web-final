pipeline {
    agent any
    stages {
        stage('Build') { 
            steps {
                sh 'mvn -B -DskipTests clean package' 
            }
        }

        stage('Deploy') { 
            steps {
                sh 'mvn -B -P tomcat7:deploy-only -e' 
            }
        }
    }
}
