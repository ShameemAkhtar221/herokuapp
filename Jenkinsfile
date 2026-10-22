pipeline {
    agent any

    parameters {
        choice(name: 'BROWSER', choices: ['chrome', 'firefox'], description: 'Browser to run tests on')
        choice(name: 'SUITE', choices: ['testng.xml', 'Smoke', 'Regression'], description: 'Full testng.xml suite, or one TestNG group')
    }

    triggers {
        pollSCM('H/5 * * * *')   // check GitHub for new commits every ~5 minutes
    }

    options {
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }

    stages {
        stage('Build') {
            steps {
                sh 'mvn -B clean compile test-compile'
            }
        }
        stage('Test') {
            steps {
                script {
                    def suiteArgs = params.SUITE == 'testng.xml' ? '-Pcross-browser' : "-Dgroups=${params.SUITE}"
                    sh "mvn -B test ${suiteArgs} -Dbrowser=${params.BROWSER} -Dheadless=true"
                }
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
            archiveArtifacts artifacts: 'target/screenshots/*.png', allowEmptyArchive: true
            // Needs the Allure Jenkins plugin and an Allure Commandline tool named 'allure' (Manage Jenkins -> Tools)
            allure includeProperties: false, commandline: 'allure', results: [[path: 'target/allure-results']]
        }
    }
}
