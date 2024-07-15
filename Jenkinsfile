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
                sh 'mvn -B -P deploy-profile-from-pom-xml tomcat7:deploy-only' 
            }
        }
    }
}
