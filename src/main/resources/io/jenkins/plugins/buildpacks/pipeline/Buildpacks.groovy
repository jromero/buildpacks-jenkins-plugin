
package io.jenkins.plugins.buildpacks.pipeline

import com.cloudbees.groovy.cps.NonCPS
import org.jenkinsci.plugins.workflow.cps.CpsScript
import org.jenkinsci.plugins.workflow.cps.CpsThread

import groovy.lang.Closure
import io.jenkins.cli.shaded.org.apache.commons.lang.builder.ReflectionToStringBuilder
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

        // creating a new instance, when we give the 'config' array in the constructor, the variables is transferred.
        BuildpacksPipelineDSL pipeline = new BuildpacksPipelineDSL(config)
       
    }

}
