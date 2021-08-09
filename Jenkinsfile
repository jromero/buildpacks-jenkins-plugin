pipeline {
    agent any
    stages {
        stage('build') {
            steps {
                script {
                    git url: 'https://github.com/buildpacks/samples'
                    
                    buildpacks {
                        builder = "cnbs/sample-builder:alpine"
                        path = "apps/java-maven"
                        imageName = "image-test:test"
                    }
                }
            }
        }
    }
}
