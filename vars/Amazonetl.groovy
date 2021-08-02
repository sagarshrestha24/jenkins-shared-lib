def call(body)
{
    def pipelineParams= [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()
pipeline {
  agent any
 environment{
    registryCredential = 'Docker_cred'
    
    gpg_secret = credentials("gpg-secret")
    gpg_trust = credentials("gpg-trust")
    gpg_passphrase = credentials("gpg-password")
 }
  stages {
    
    stage("Master") {
      when {
        branch 'master'

      }
      steps {
        echo 'we are in Master branch'
        
        
      }

    }
    stage("Develop") {
      when {
        branch 'develop'

      }
      steps {
        echo 'we are in Develop branch'
      }

    }
    stage("Test") {
      when {
        branch 'test'

      }
      steps {
        echo 'we are in test branch'

      }

    }
   stage('Decrypt a master secret file')
    {
      when {
        branch 'master'
      }
      steps{
        
        script{
          sh '''
               
                
               cd config/
               gpg --batch --import $gpg_secret
               
                git secret reveal -p $gpg_passphrase
                '''
                
          
        }
      }
    }
    stage('Decrypt a develop secret file')
    {
      when {
        branch 'develop'
      }
      steps{
        
        script{
          sh '''
               
                
               cd config/
               gpg --batch --import $gpg_secret
               
                git secret reveal -p $gpg_passphrase
                '''
                
          
        }
      }
    }
    stage('Decrypt a test secret file')
    {
      when{
      branch 'test'
      }
      steps{
        
        script{
          sh '''
               
                
               cd config/
               gpg --batch --import $gpg_secret
               
                git secret reveal -p $gpg_passphrase
                '''
                
          
        }
      }
    }
   
 
 stage('Building a image for amazon-associate-etl ') {
      when {
        changeset "amazon-associate-etl/docker-images/amazon-associate-service/**"
      }
      steps {
        script {
          docker.withRegistry('', registryCredential) {
            sh '''
               cd amazon-associate-etl/docker-images/amazon-associate-service/
               make build-image '''

          }
        }

      }
      
    }
    stage('Test a image for  amazon-associate-etl ') {
      when {
        changeset "amazon-associate-etl/docker-images/amazon-associate-service/**"
      }
      steps {
        script {
          
            sh '''
               cd amazon-associate-etl/docker-images/amazon-associate-service/
               make test-image '''

          
        }

      }
      
    }
    stage('Push a image amazon-associate-etl ') {
      when {
        changeset "amazon-associate-etl/docker-images/amazon-associate-service/**"
      }
      steps {
        script {
          docker.withRegistry('', registryCredential) {
            sh '''
               cd amazon-associate-etl/docker-images/amazon-associate-service/
               make push-image '''

          }
        }

      }
      
    }
    stage('Pre-deploy image for amazon-associate-etl ') {
      when {
        changeset "amazon-associate-etl/docker-images/amazon-associate-service/**"
      }
      steps {
        script {
          
            sh '''
               cd amazon-associate-etl/docker-images/amazon-associate-service/
               make pre-deploy-image '''

        }
        }

      }
      
    
    stage('deploy image for amazon-associate-etl ') {
      when {
        changeset "amazon-associate-etl/docker-images/amazon-associate-service/**"
      }
      steps {
        script {
          docker.withRegistry('', registryCredential) {
            sh '''
               cd amazon-associate-etl/docker-images/amazon-associate-service/
               make deploy-dockerimage '''

          }
        }

      }
      
    }
    stage('Post-deploy image for amazon-associate-etl ') {
      when {
        branch 'test'
        changeset "amazon-associate-etl/docker-images/amazon-associate-service/**"
      }
      steps {
        script {
        
            sh '''
               cd amazon-associate-etl/docker-images/amazon-associate-service/
               make post-deploy-image '''

          
        }

      }
      
    }

}
}
}
