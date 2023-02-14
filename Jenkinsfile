pipeline {
  agent { label ('ec2-fleet-php')}
  stages {
    stage('Pre Deployment') {
      steps {
            slackSend (
                channel: "prj-oneplat-rice-market-dev-notifier",
                color: '#FFFF00',
                message: "Starting Job:    » ${env.JOB_NAME} [${env.BUILD_NUMBER}]\nBrch     » ${env.GIT_BRANCH}",
                teamDomain: 'nalsdn',
                tokenCredentialId: 'nalsdn-rmt-slack'
            )
        script {
          env.GIT_TAG = sh(returnStdout: true, script: 'git tag --points-at ${GIT_COMMIT} || :').trim()
          env.GIT_COMMIT_MSG = sh(script: 'git log -1 --pretty=%B ${GIT_COMMIT}', returnStdout: true).trim()
        }
      }
    }
    stage('Deploy-Dev') {
      when {
        expression {env.GIT_BRANCH == 'origin/develop'}
      }
      steps {
        bitbucketStatusNotify(buildState: 'INPROGRESS')
        sh 'ANSIBLE_HOST_KEY_CHECKING=false ansible-playbook -i playbooks/inventories/develop/hosts playbooks/playbook.yaml'
      }
    }  
  }
    post {
        success {
            slackSend (
                channel: "prj-oneplat-rice-market-dev-notifier",
                color: '#00FF00',
                message: "Status   » SUCCESS\nJob       » ${env.JOB_NAME} [${env.BUILD_NUMBER}]\nBrch     » ${env.GIT_BRANCH}\n    » ${env.GIT_COMMIT} [${GIT_COMMIT_MSG}]",
                teamDomain: 'nalsdn',
                tokenCredentialId: 'nalsdn-rmt-slack'
            )
            bitbucketStatusNotify(buildState: 'SUCCESSFUL')
          }
        failure {
            slackSend (
                channel: "prj-oneplat-rice-market-dev-notifier",
                color: '#FF0000',
                message: "Status   » FAILED\nJob       » ${env.JOB_NAME} [${env.BUILD_NUMBER}]\nBrch     » ${env.GIT_BRANCH}\n    » ${env.GIT_COMMIT} [${GIT_COMMIT_MSG}]",
                teamDomain: 'nalsdn',
                tokenCredentialId: 'nalsdn-rmt-slack'
            )
            bitbucketStatusNotify(buildState: 'FAILED')  
        }
    }
}  
