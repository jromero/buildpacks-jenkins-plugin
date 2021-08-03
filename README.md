# USAGE

Basically, three parameters(path, image name and builder image) are needed for an application to be converted into an OCI image by buildpacks. So, in buildpacks DSL you can get an OCI image just by passing these three data as parameters.

In the simplest way, a DSL example looks like the following.
    
    script {
   
        git url:'https://github.com/some/repository'

        buildpacks {
            path = 'relative/path'
            imageName = 'test-image:latest'
            builder = 'cnbs/sample-builder:alpine'
        }

    }


## Parameters

### Path

Takes a directory as a string parameter. The given directory must be in the Jenkins building workspace. It is intended for use with Git.

Example:

    script {
        git url:'https://github.com/buildpacks/samples'

        buildpacks {
            path = 'apps/java-maven'
        }
    }

### Image Name

Takes an image name as a string. If a tag is not specified at the end of the image name, the tag is assigned 'latest' by default.

Example:

    script {
        buildpacks {
            imageName = 'test-image:latest'
        }
    }

### Builder

Takes a build image name as a string. It will try to download from the docker registry system(it requires 'docker login' command) if not downloaded locally.

Example:

    script {
        buildpacks {
            builder = 'cnbs/sample-builder:alpine'
        }
    }

# INSTALLATION

Jenkins plugins work with Java 8 and the project was developed with maven infrastructure. So we'll start by setting up these two first.

### JDK 8
You can download JDK 8 to your computer from [this link](https://www.oracle.com/tr/java/technologies/javase/javase-jdk8-downloads.html).  

For installation, you can follow the documentation in [this link](https://docs.datastax.com/en/jdk-install/doc/jdk-install/installOpenJdkDeb.html).


### MAVEN 

You can download maven to your machine from [this link](https://maven.apache.org/download.cgi).  

For installation, you can follow the steps in [this link](https://maven.apache.org/install.html).

    

# DEVELOPMENT

Generally people use IntelliJ IDEA environment for development. You can find the configurations for the IntelliJ IDEA [here](https://www.jenkins.io/doc/developer/building/intellij/).  

If you are looking for something light, you can install [VSCode](https://code.visualstudio.com/). And [Java plugin packet](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack) will make your job much easier.