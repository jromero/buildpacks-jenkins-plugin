package dsl

// The call(body) method in any file in workflowLibs.git/vars is exposed as a
// method with the same name as the file.
def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    /** Run the build scripts */

    try {

        echo "${config.builder}"

    } catch (Exception rethrow) {
        failureDetail = failureDetail(rethrow)
        throw rethrow
    }

}

def runViaDocker(config) {
    node(config.machine) {
        docker.image(config.docker_image).inside {
            runScripts(config)
        }
    }
}


/** Run the before/script combination */
def runScripts(config) {
    envList = []
    for ( e in config.env ) {
        envList.add("${e.getKey()}=${e.getValue()}")
    }
    withEnv(envList) {

        /* checkout the codes */
        if (config.git_repo == null) {
            checkout scm
        } else {
            git config.git_repo
        }

        /* run the basic build steps */
        if (config.before_script != null) {
            sh config.before_script
        }
        sh config.script
        if (config.after_script != null) {
            sh config.after_script
        }


    }
}

/**
 * Read the detail from the exception to be used in the failure message
 * https://issues.jenkins-ci.org/browse/JENKINS-28119 will give better options.
 */
def failureDetail(exception) {
    /* not allowed to access StringWriter
    def w = new StringWriter()
    exception.printStackTrace(new PrintWriter(w))
    return w.toString();
    */
    return exception.toString()
}
