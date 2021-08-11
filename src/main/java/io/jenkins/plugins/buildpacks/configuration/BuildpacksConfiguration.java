package io.jenkins.plugins.buildpacks.configuration;

import hudson.Extension;
import hudson.ExtensionList;
import hudson.util.FormValidation;
import jenkins.model.GlobalConfiguration;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Example of Jenkins global configuration.
 */
@Extension
public class BuildpacksConfiguration extends GlobalConfiguration {

    /** @return the singleton instance */
    public static BuildpacksConfiguration get() {
        return ExtensionList.lookupSingleton(BuildpacksConfiguration.class);
    }

    private String relative;
    private String fileSystem;
    private String url;
    private String docker;
    private String cnbBuilderResource;
    private String cnbRegistryResource;

    public BuildpacksConfiguration() {
        // When Jenkins is restarted, load any saved configuration from disk.
        load();
    }

    public String getRelative() {
        return relative;
    }

    public String getFileSystem() {
        return fileSystem;
    }

    public String getURL() {
        return url;
    }

    public String getDocker() {
        return docker;
    }

    public String getCnbBuilderResource() {
        return cnbBuilderResource;
    }

    public String getCnbRegistryResource() {
        return cnbRegistryResource;
    }

    /**
     * Together with {@link #getRelative}, binds to entry in {@code config.jelly}.
     * @param relative the new value of this field
     */
    @DataBoundSetter
    public void setRelative(String relative) {
        this.relative = relative;
        save();
    }

    @DataBoundSetter
    public void setFileSystem(String fileSystem) {
        this.fileSystem = fileSystem;
        save();
    }

    @DataBoundSetter
    public void setURL(String url) {
        this.url = url;
        save();
    }

    @DataBoundSetter
    public void setDocker(String docker) {
        this.docker = docker;
        save();
    }

    @DataBoundSetter
    public void setCnbBuilderResource(String cnbBuilderResource) {
        this.cnbBuilderResource = cnbBuilderResource;
        save();
    }

    @DataBoundSetter
    public void setCnbRegistryResource(String cnbRegistryResource) {
        this.cnbRegistryResource = cnbRegistryResource;
        save();
    }

    public FormValidation doCheckRelative(@QueryParameter String value) {

        if(!value.isEmpty()){

            File f = new File(value);
            if (!f.exists()) 
                return FormValidation.error("Path is not available. It must be in the form of: <path>");  

        }

        return FormValidation.ok();
    }

    public FormValidation doCheckFileSystem(@QueryParameter String value) {

        if(!value.isEmpty()){
            
            Pattern pattern = Pattern.compile("file://(.*)*/(.*)+", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(value);
            if(matcher.find()) {
                File f = new File(value);
                if (!f.isFile()) 
                    return FormValidation.error("Filesystem is not available. It must be in the form of: file://[<host>]/<path>");  
    
            } 
            else 
                return FormValidation.error("Filesystem variable must be in the form of: file://[<host>]/<path>");  
            
        }

        return FormValidation.ok();
    }
    
    public FormValidation doCheckURL(@QueryParameter String value) {

        if(!value.isEmpty()){

            Pattern pattern = Pattern.compile("http(s)?://(.*)*/(.*)+", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(value);
            if(!matcher.find())
                return FormValidation.error("URL variable must be in the form of: http[s]://<host>/<path>");  

        }

        return FormValidation.ok();
    }

    public FormValidation doCheckDocker(@QueryParameter String value) {

        if(!value.isEmpty()){

            Pattern pattern = Pattern.compile("docker://(.*)*/(.*)+((:.*){1}(⏐@(.*))?)?", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(value);
            if(!matcher.find())
                return FormValidation.error("Docker variable must be in the form of: docker://[<host>]/<path>[:<tag>⏐@<digest>]");  

        }

        return FormValidation.ok();
    }

    public FormValidation doCheckCnbBuilderResource(@QueryParameter String value) {

        if(!value.isEmpty()){

            Pattern pattern = Pattern.compile("urn:cnb:builder((:.*){1}⏐(@(.*){1})?)?", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(value);
            if(!matcher.find()) 
                return FormValidation.error("CNB Builder Resource variable must be in the form of: urn:cnb:builder[:<id>[@<version>]]");  

        }

        return FormValidation.ok();
    }

    public FormValidation doCheckCnbRegistryResource(@QueryParameter String value) {

        if(!value.isEmpty()){

            Pattern pattern = Pattern.compile("urn:cnb:registry((:.*){1}⏐(@(.*){1})?)?", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(value);
            if(!matcher.find()) 
                return FormValidation.error("CNB Registry Resource variable must be in the form of: urn:cnb:registry[:<id>[@<version>]]");  

        }

        return FormValidation.ok();
    }
}
