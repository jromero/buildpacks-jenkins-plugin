package io.jenkins.plugins.buildpacks.pipeline;

import hudson.Extension;

import org.jenkinsci.plugins.scriptsecurity.sandbox.whitelists.StaticWhitelist;
import org.jenkinsci.plugins.scriptsecurity.sandbox.whitelists.ProxyWhitelist;
import org.jenkinsci.plugins.workflow.cps.GlobalVariable;
import org.jenkinsci.plugins.workflow.cps.CpsScript;
import org.jenkinsci.plugins.workflow.cps.EnvActionImpl;

import java.util.LinkedHashMap;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.Map;
import java.io.File;

import dev.snowdrop.buildpack.BuildpackBuilder.LogReader;
import dev.snowdrop.buildpack.BuildpackBuilder;

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
        
        private PrintStream ps = null;
        private EnvActionImpl env = null;

        private String builder = "";
        private String path = "";
        private String imageName = "";
        
        public BuildpacksPipelineDSL(){}
        
        /**
         * 
         * @param c
         * @throws Exception
         */
        public BuildpacksPipelineDSL(LinkedHashMap<String, Object> c, PrintStream ps, EnvActionImpl env) throws Exception {

            this.ps = ps;
            this.env = env;
            
            extractParameters(c);

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

        public String getWorkspace(){
            return this.env.getProperty("WORKSPACE");
        }

        /**
         * 
         * @param c
         * @return
         */
        public int extractParameters(LinkedHashMap<String, Object> c){
            
            
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
                        //setPath(Paths.get(getPath(), it.getValue().toString()).toString());
                        //setPath(it.getValue().toString());
                        setPath(Paths.get(getWorkspace(), it.getValue().toString()).toString());
                        break;
                    /*case "withGit":
                        // if git is used the path is updated.
                        setPath(Paths.get(getWorkspace(), getPath()).toString());
                        break;*/
                    default:
                        break;
                }

            return 0;

        }
        
        /**
         * 
         * @throws Exception
         */
        public void checkParameters() throws Exception {

            if(getPath().isEmpty())
                throw new Exception("The variable 'path' cannot be empty.");
            if(getBuilder().isEmpty())
                throw new Exception("The variable 'builder' cannot be empty.");
            if(getImageName().isEmpty())
                throw new Exception("The variable 'imageName' cannot be empty.");
        
        }
        
        /**
         * 
         * @return
         * @throws Exception
         */
        public BuildpackBuilder build() throws Exception {
          
            checkParameters();
            
            BuildpacksLogger logger = new BuildpacksLogger(this.ps);

            BuildpackBuilder bpb = BuildpackBuilder.get();

            bpb = bpb.withContent(new File(getPath()));

            bpb = bpb.withBuildImage(getBuilder());

            bpb = bpb.withFinalImage(getImageName());
            
            bpb.build(logger);

            return bpb;
        } 
        
    }

    @Extension
    public static class BuildpacksLogger implements LogReader {
        
        private PrintStream ps;

        public BuildpacksLogger(){}

        public BuildpacksLogger(PrintStream ps){
            this.ps = ps;
        }

        @Override
        public boolean stripAnsiColor() {
            return true;
        }

        @Override
        public void stdout(String message) {
            if (message.endsWith("\n")) {
                message = message.substring(0, message.length() - 1);
            }           
            ps.println(message);            
        }

        @Override
        public void stderr(String message) {
            if (message.endsWith("\n")) {
                message = message.substring(0, message.length() - 1);
            }
            ps.println(message);
        } 

        
    }

}
