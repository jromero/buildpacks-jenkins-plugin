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
