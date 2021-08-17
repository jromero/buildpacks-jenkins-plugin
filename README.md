# CONTENT

- [CONTENT](#content)
- [INSTALLATION](#installation)
    - [Docker.sock access for Ubuntu](#dockersock-access-for-ubuntu)
- [USAGE](#usage)
    - [Parameters](#parameters)
        - [Path](#path)
        - [Image Name](#image-name)
        - [Builder](#builder)
        - [Environment Variable(s)](#environment-variables)
- [BASIC EXAMPLE](#basic-example)  
- [DEVELOPMENT](#development)  
    - [INSTALLATION](#installation-1)
        - [JDK 8](#jdk-8)
        - [MAVEN](#maven)
        - [Running the plugin locally](#running-the-plugin-locally)
        - [How do I compile codes to the .hpi file ?
](#how-do-I-compile-codes-to-the-hpi-file-)

# INSTALLATION

The plugin is currently not released on jenkins. So you have to install it locally.

1. First, download the [hpi](https://github.com/fatiiates/buildpacks-jenkins-plugin/releases/download/v0.1.0/buildpacks.hpi) file.

        wget https://github.com/fatiiates/buildpacks-jenkins-plugin/releases/download/v0.1.0/buildpacks.hpi

2. Then go to Jenkins Dashboard > Manage Jenkins > Manage Plugins. You should have reached the plugin manager.

    ![step2.1](https://user-images.githubusercontent.com/51250249/128188400-5243dde3-26db-4a41-9356-73f738327091.png)
    ![step2.2](https://user-images.githubusercontent.com/51250249/128188405-a2f44321-22a9-4276-b7aa-af1d7420a916.png)


3. Next, click on the ```advanced``` tab.

    ![step3](https://user-images.githubusercontent.com/51250249/128188407-82dfd79b-af90-4371-901b-10aff76b92f3.png)

4. Upload the 'buildpacks.hpi' file that you downloaded earlier to the section below in this tab and press the upload button.

    ![step4](https://user-images.githubusercontent.com/51250249/128188408-c7275110-5629-4fe3-88d3-a02c6e344f1d.png)

5. After pressing the upload button, it will also install some dependencies for the buildpack plugin in the window that opens. When the buildpacks shows as ```success``` as below, it means that the plugin has been installed successfully.

    ![step5](https://user-images.githubusercontent.com/51250249/128189292-af39a65d-3322-44bc-9565-46ae75fa19b5.png)

After successfully installing the plugin

This plugin needs access to the docker socket. Jenkins cannot access this socket by default.

## Docker.sock access for Ubuntu

By default the group of docker.sock is assigned 'docker'. But to be sure, you can check with the command below.

    ls -l /var/run/docker.sock

In the returned output the group name is the 4th parameter.

    $ ls -l /var/run/docker.sock   
    srw-rw---- 1 tati docker 0 Ağu  4 13:10 /var/run/docker.sock  

As seen in the output above, if it has a docker group, you can include the jenkins user in the docker group with the following command.

    sudo usermod -aG docker jenkins

Congratulations! If you have successfully completed this step, you can now use the plugin.

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

### Environment Variable(s)

Takes environment variables to be used during build as array or string. If it is not passed as a parameter, it means that it does not need an additional environment variables.

Example:

    script {
        buildpacks {
            env = "ENV1=test1"
        }
    }

or

    script {
        buildpacks {
            env = [
                "ENV1=test1",
                "ENV2=test2"
            ]
        }
    }

or you can pass filename(The given file must be in the Jenkins building workspace. It is intended for use with Git.)

    script {
        buildpacks {
            envFile = "./.env"
        }
    }

# BASIC EXAMPLE

This script downloads data from remote git repository and runs buildpack lifecycle according to given parameters.


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

After a successful run it will give the following output

![step6](https://user-images.githubusercontent.com/51250249/128190301-670b54c3-6fba-4a47-8b72-86586773176e.png)

# DEVELOPMENT

Generally people use IntelliJ IDEA environment for development. You can find the configurations for the IntelliJ IDEA [here](https://www.jenkins.io/doc/developer/building/intellij/).  

If you are looking for something light, you can install [VSCode](https://code.visualstudio.com/). And [Java plugin packet](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack) will make your job much easier.

## INSTALLATION

Jenkins plugins work with Java 8 and the project was developed with maven infrastructure. So we'll start by setting up these two first.

### JDK 8
You can download JDK 8 to your computer from [this link](https://www.oracle.com/tr/java/technologies/javase/javase-jdk8-downloads.html).  

For installation, you can follow the documentation in [this link](https://docs.datastax.com/en/jdk-install/doc/jdk-install/installOpenJdkDeb.html).


### MAVEN 

You can download maven to your machine from [this link](https://maven.apache.org/download.cgi).  

For installation, you can follow the steps in [this link](https://maven.apache.org/install.html).

## Running the plugin locally

Firstly, install your dependencies on your computer with the command below and make sure there are no errors.

    mvn install

Then verify the accuracy of the tests with the following command.

    mvn verify

And now run your Jenkins instance locally with the following command. By default it will start running on port 8080.

    mvn hpi:run

If you want to run it on a different port, you can use the command below.

    mvn hpi:run -Djetty.port=PORT

## How do I compile codes to the .hpi file ?

You can start by cloning the repository first.

    git clone https://github.com/fatiiates/buildpacks-jenkins-plugin

Then run the commands below by entering the main directory of the repository.

    mvn install
    mvn verify

If the commands are run successfully, the .hpi file will be created in the target directory as follows.

![step8](https://user-images.githubusercontent.com/51250249/128193333-3a91ff5d-03cf-4beb-bab9-fef93a27c341.png)
