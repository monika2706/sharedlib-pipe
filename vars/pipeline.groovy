def call(Map stageParams) {
pipeline{
agent any
tools{
maven"mymavan"
}
environment{
    def tomcatWeb = 'C:\\Program Files\\Apache Software Foundation\\Tomcat 8.5\\webapps'
    def tomcatBin = 'C:\\Program Files\\Apache Software Foundation\\Tomcat 8.5\\bin'
}
stages{
stage("checkoutfromgit"){
steps{
git url:'https://github.com/monika2706/DevOpsClassCodes.git'
}
}
stage("codecompile & package"){
steps{
bat "mvn clean"
bat "mvn compile"
bat "mvn package"
}
}
stage("junittest"){
steps{
bat "mvn test"
}
}
stage('Code Coverage') {
steps {
jacoco()
}
}
stage("Code Quality") {
     steps {
           bat "mvn sonar:sonar -Dsonar.login=4f2f299ae6a598652c472cfd688a13d5fef080f2 -Dsonar.host.url=http://localhost:9000/"
     }
   }
stage('Upload *.war file to Artifactory') {
    steps {
           rtUpload (
                    serverId: "Artifactory" ,
                    spec: """{
                            "files": [
                                    {
                                        "pattern": "*.war",
                                        "target": "libs-snapshot-local"
                                    }
                                ]
                            }"""
                )
    }
  }
  
  stage ('Publish build info') {
            steps {
                rtPublishBuildInfo (
                    serverId: "Artifactory"
                )
            }
        }
stage('Deploy to Tomcat'){
steps{
     bat "copy target\\addressbook.war \"${tomcatWeb}\\addressbook.war\""
  }
  }
  }
  }
  }
