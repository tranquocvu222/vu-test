pipeline {
  agent any
  stages {
    stage('Pre Deployment') {
      steps {
            slackSend (
                channel: "prj-rmt-auction-development-notifier",
                color: '#FFFF00',
                message: "Starting Job:    » ${env.JOB_NAME} [${env.BUILD_NUMBER}]\nBranch     » ${env.GIT_BRANCH}",
                teamDomain: 'nalsdn',
                tokenCredentialId: 'nalsdn-rmt-auction-slack'
            )
        script {
          env.GIT_TAG = sh(returnStdout: true, script: 'git tag --points-at ${GIT_COMMIT} || :').trim()
          env.GIT_COMMIT_MSG = sh(script: 'git log -1 --pretty=%B ${GIT_COMMIT}', returnStdout: true).trim()
        }
      }
    }
    stage('Deploy dev environment') {
      when {
         allOf {
             not {
                 expression { env.GIT_TAG =~ /(qa)/ };
             }
         }
      }
      steps {
        bitbucketStatusNotify(buildState: 'INPROGRESS')
        sh 'ANSIBLE_HOST_KEY_CHECKING=false ansible-playbook -i playbooks/inventories/dev/hosts playbooks/playbook.yaml'
      }
    }
    stage('Deploy qa environment') {
      when {
         expression { env.GIT_TAG ==~ /qa\/.*/ }
      }
      steps {
         bitbucketStatusNotify(buildState: 'INPROGRESS')
         sh 'ANSIBLE_HOST_KEY_CHECKING=false ansible-playbook -i playbooks/inventories/qa/hosts playbooks/playbook.yaml'
       }
     }
  }
    post {
        success {
            slackSend (
                channel: "prj-rmt-auction-development-notifier",
                color: '#00FF00',
                message: "Status   » SUCCESS\nJob       » ${env.JOB_NAME} [${env.BUILD_NUMBER}]\nBranch     » ${env.GIT_BRANCH}\n    » ${env.GIT_COMMIT} [${GIT_COMMIT_MSG}]",
                teamDomain: 'nalsdn',
                tokenCredentialId: 'nalsdn-salon-job-slack'
            )
            bitbucketStatusNotify(buildState: 'SUCCESSFUL')
          }
        failure {
            slackSend (
                channel: "prj-rmt-auction-development-notifier",
                color: '#FF0000',
                message: "Status   » FAILED\nJob       » ${env.JOB_NAME} [${env.BUILD_NUMBER}]\nBranch     » ${env.GIT_BRANCH}\n    » ${env.GIT_COMMIT} [${GIT_COMMIT_MSG}]",
                teamDomain: 'nalsdn',
                tokenCredentialId: 'nalsdn-salon-job-slack'
            )
            bitbucketStatusNotify(buildState: 'FAILED')
        }
    }
}
