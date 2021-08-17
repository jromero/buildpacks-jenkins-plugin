package io.jenkins.plugins.buildpacks.configuration;

import hudson.Extension;
import hudson.ExtensionList;
import hudson.util.FormValidation;
import jenkins.model.GlobalConfiguration;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import java.io.File;

@Extension
public class BuildpacksConfiguration extends GlobalConfiguration {

    /** @return the singleton instance */
    public static BuildpacksConfiguration get() {
        return ExtensionList.lookupSingleton(BuildpacksConfiguration.class);
    }

    private String cnbGlobalVariable;

    public BuildpacksConfiguration() {
        // When Jenkins is restarted, load any saved configuration from disk.
        load();
    }

    public String getCnbGlobalVariable() {
        return cnbGlobalVariable;
    }

    /**
     * Together with {@link #getCnbGlobalVariable}, binds to entry in {@code config.jelly}.
     * @param cnbGlobalVariable the new value of this field
     */
    @DataBoundSetter
    public void setCnbGlobalVariable(String cnbGlobalVariable) {
        this.cnbGlobalVariable = cnbGlobalVariable;
        save();
    }

    public FormValidation doCheckCnbGlobalVariable(@QueryParameter String value) {

        if(!value.isEmpty()){

            File f = new File(value);
            if (!f.exists()) 
                return FormValidation.error("Path is not available. It must be in the form of: <path>");  

        }

        return FormValidation.ok();
    }

}
