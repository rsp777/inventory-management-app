node {
    def mvnHome
    stage('Preparation') { // for display purposes
        // Get some code from a GitHub repository
        git branch: 'feature-regsisteration/invnapp',url: 'https://github.com/rsp777/inventory-management-app.git'
        // Get the Maven tool.
        // ** NOTE: This 'M3' Maven tool must be configured
        // **       in the global configuration.
        mvnHome = tool 'M3'
    }
    stage('Build') {
        // Run the maven build
        withEnv(["MVN_HOME=$mvnHome"]) {
            if (isUnix()) {
                sh '"$MVN_HOME/bin/mvn" -Dmaven.test.failure.ignore clean package'
            } else {
                bat(/"%MVN_HOME%\bin\mvn" -Dmaven.test.failure.ignore clean package/)
            }
        }
    }
    stage('Archive Artifacts') {
        archiveArtifacts 'target/*.jar'
        //withEnv(["MVN_HOME=$mvnHome"]){
        //sh '"$MVN_HOME/bin/mvn" -Dmaven.test.failure.ignore deploy'
        }
    }
}
