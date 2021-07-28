
package io.jenkins.plugins.buildpacks.pipeline

import org.jenkinsci.plugins.workflow.cps.CpsScript

import groovy.lang.Closure

import io.jenkins.plugins.buildpacks.pipeline.BuildpacksDSL.BuildpacksPipelineDSL

class Buildpacks implements Serializable {

    private org.jenkinsci.plugins.workflow.cps.CpsScript script

    public Buildpacks(org.jenkinsci.plugins.workflow.cps.CpsScript script){
        this.script = script
    }

    // first executed method is similar to main method in java
    public void call(final Closure body) {

        // The config array is the array that holds the variables.
        def config = [:]
        body.resolveStrategy = Closure.DELEGATE_FIRST
        body.delegate = config
        body()

        def logger = this.script.getContext(TaskListener.class).getLogger()

        // creating a new instance, when we give the 'config' array in the constructor, the variables is transferred.
        BuildpacksPipelineDSL pipeline = new BuildpacksPipelineDSL(config, logger)
        pipeline.build()

    }

}
