package io.jenkins.plugins.buildpacks.pipeline;

import hudson.Extension;

import org.jenkinsci.plugins.workflow.cps.GlobalVariable;
import org.jenkinsci.plugins.workflow.cps.CpsScript;
import org.jenkinsci.plugins.scriptsecurity.sandbox.whitelists.ProxyWhitelist;
import org.jenkinsci.plugins.scriptsecurity.sandbox.whitelists.StaticWhitelist;

import java.io.IOException;
import java.io.File;
import dev.snowdrop.buildpack.BuildpackBuilder;
//import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
//import org.jenkinsci.plugins.dockerbuildstep.cmd.CreateContainerCommand;
@Extension
public class BuildpacksDSL extends GlobalVariable {

    private static final String BUILDPACKS = "buildpacks";

    @Override
    public String getName() {
        return BUILDPACKS;
    }
    
    @Override
    public Object getValue(CpsScript script) throws Exception {
        return script.getClass()
                .getClassLoader()
                .loadClass("io.jenkins.plugins.buildpacks.pipeline.Buildpacks")
                .getConstructor(CpsScript.class)
                .newInstance(script);
    }

    @Extension
    public static class MiscWhitelist extends ProxyWhitelist {
        /**
         * Methods to add to the script-security whitelist for this plugin to work.
         *
         * @throws IOException
         */
        
        public MiscWhitelist() throws IOException {
            super(new StaticWhitelist(
                    "method java.util.Map size",
                    "method java.util.Map keySet",
                    "method java.util.Map values"
            ));
        }
    }

    @Extension
    public static class BuildpacksPipelineDSL {     
        
        private String builder = null;
        private String path = null;
        private String imageName = null;
        
        public BuildpacksPipelineDSL(){}
        
        public BuildpacksPipelineDSL(LinkedHashMap<String, Object> c) throws Exception {
            // config variables is contains builder, path, imageName etc.
            LinkedHashMap<String, Object> config = new LinkedHashMap<String, Object>(c);
            
            for (Map.Entry<String, Object> it : config.entrySet())
                switch (it.getKey()) {
                    case "builder":
                        setBuilder(it.getValue().toString());
                        break;
                    case "imageName":
                        setImageName(it.getValue().toString());
                        break;
                    case "path":
                        setPath(it.getValue().toString());
                        break;
                    default:
                        break;
                }
            
            BuildpackBuilder.get()
                .withContent(new File(getPath()))
                .withBuildImage(getBuilder())
                .withFinalImage(getImageName())
                .build();

        }
        public String getBuilder(){
            return this.builder;
        }
    
        public String getPath(){
            return this.path;
        }
    
        public String getImageName(){
            return this.imageName;
        }

        public void setBuilder(String b){
            this.builder = b;
        }
    
        public void setPath(String p){
            this.path = p;
        }
    
        public void setImageName(String i){
            this.imageName = i;
        }
    
    }

}
