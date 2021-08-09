package io.jenkins.plugins.buildpacks.pipeline;

import hudson.Extension;

import org.jenkinsci.plugins.scriptsecurity.sandbox.whitelists.StaticWhitelist;
import org.jenkinsci.plugins.scriptsecurity.sandbox.whitelists.ProxyWhitelist;
import org.jenkinsci.plugins.workflow.cps.GlobalVariable;
import org.jenkinsci.plugins.workflow.cps.CpsScript;
import org.jenkinsci.plugins.workflow.cps.EnvActionImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.io.BufferedReader;
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
        
        /* jenkins variables*/

        // jenkins logger
        private PrintStream ps = null;
        // jenkins env variables
        private EnvActionImpl jenkinsEnv = null;


        /* DSL variables */

        private String builder = "";
        private String path = "";
        private String imageName = "";
        private Path envFile = Paths.get("");
        private Map<String, String> env = new HashMap<>();;

        public BuildpacksPipelineDSL(){}
        
        /**
         * 
         * @param c
         * @throws Exception
         */
        public BuildpacksPipelineDSL(LinkedHashMap<String, Object> c, PrintStream ps, EnvActionImpl jenkinsEnv) throws Exception {

            this.ps = ps;
            this.jenkinsEnv = jenkinsEnv;
            
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

        public Path getEnvFile(){
            return this.envFile;
        }

        public Map<String, String> getEnv(){
            return this.env;
        }

        public void setBuilder(String b){
            this.builder = b;
        }
    
        public void setPath(String p){
            this.path = p;
        }
    
        public void setEnvFile(Path i){
            this.envFile = i;
        }

        public void setImageName(String i){
            this.imageName = i;
        }

        public void setEnv(Map<String, String> e){
            this.env = e;
        }

        public String getWorkspace(){
            return this.jenkinsEnv.getProperty("WORKSPACE");
        }

        /**
         * 
         * @param c
         * @return
         * @throws Exception
         */
        public int extractParameters(LinkedHashMap<String, Object> c) throws Exception{
            
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
                        setPath(Paths.get(getWorkspace(), it.getValue().toString()).toString());
                        break;
                    case "env":
                        extractEnvironmentVariables((ArrayList)it.getValue());
                        break;
                    case "envFile":
                        setEnvFile(Paths.get(getWorkspace(), it.getValue().toString()));
                        extractEnvironmentVariablesFromFile(getEnvFile());
                        break;
                    default:
                        break;
                }

            return 0;

        }
        
        public void extractEnvironmentVariables(ArrayList<String> al) throws Exception{
            Map<String, String> envs = new HashMap<>();
    
            int i = 0;
            for (Object env : al) { 
                String e = env.toString();
                String[] s = e.split("=", 2);
                if(e.isEmpty())
                    throw new Exception(String.format("element %d in env variable is empty", i));
                else if(s.length == 2){
    
                    if(s[0].isEmpty() || s[1].isEmpty())
                        throw new Exception(String.format("Error detected in '%s'(element %d in env variable) environment variable definition", env, i));
                    
                    envs.put(s[0], s[1]);
                    
                }
                else if(s.length == 1){
                    
                    if(s[0].isEmpty())
                        throw new Exception(String.format("Error detected in '%s'(element %d in env variable) environment variable definition", env, i));
    
                    if(System.getenv(s[0]) == null)
                        throw new Exception(String.format("The env variable '%s' is not found in system environment variables", env));
                    else if(System.getenv(s[0]).isEmpty())
                        throw new Exception(String.format("The env variable '%s' exists in environment variables but it's empty", i));
                    envs.put(s[0], System.getenv(s[0]));
    
                }
                i++;
            }
            setEnv(envs);
            //envs.forEach((key, value) -> System.out.println(key + ":" + value));
        }
    
        public void extractEnvironmentVariablesFromFile(Path p) throws Exception{ 
      
            BufferedReader br = Files.newBufferedReader(p, StandardCharsets.UTF_8);

            ArrayList<String> al = new ArrayList<>();
            
            String row;
            while ((row = br.readLine()) != null)
                al.add(row);
            br.close();

            extractEnvironmentVariables(al);
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
            
            if(this.env.size() > 0)
                bpb = bpb.withEnv(this.env);

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
