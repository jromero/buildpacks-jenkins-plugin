package io.jenkins.plugins.buildpacks.pipeline;

import hudson.Extension;
import io.jenkins.plugins.buildpacks.configuration.BuildpacksConfiguration;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.File;

import dev.snowdrop.buildpack.BuildpackBuilder.LogReader;
import dev.snowdrop.buildpack.BuildpackBuilder;

@Extension
public class BuildpacksDSL extends GlobalVariable {

    @Override
    public String getName() {
        return "buildpacks";
    }

    @Override
    public Object getValue(CpsScript script) throws Exception {
        return script.getClass().getClassLoader().loadClass("io.jenkins.plugins.buildpacks.pipeline.Buildpacks")
                .getConstructor(CpsScript.class).newInstance(script);
    }

    @Extension
    public static class MiscWhitelist extends ProxyWhitelist {

        /**
         * Methods to add to the script-security whitelist for this plugin to work
         *
         * @throws IOException
         */
        public MiscWhitelist() throws IOException {
            super(new StaticWhitelist("method java.util.Map size", "method java.util.Map keySet",
                    "method java.util.Map values"));
        }
    }

    @Extension
    public static class BuildpacksPipelineDSL {

        /* jenkins variables */

        private PrintStream ps = null;              // jenkins logger
        private EnvActionImpl jenkinsEnv = null;    // jenkins env variables

        /* Global Configuration */

        private BuildpacksConfiguration conf = BuildpacksConfiguration.get();

        /* DSL variables */

        private String builder = "";
        private String path = "";
        private String imageName = "";
        private Path envFile = Paths.get("");
        private Map<String, String> env = new HashMap<>();
        private ArrayList<String> buildpack = new ArrayList<String>();


        public BuildpacksPipelineDSL() {
        }

        /**
         * This constructor takes dsl parameters, logger and envs from Jenkins and
         * extracts them to local variables.
         * 
         * @param c
         * @throws Exception
         */
        public BuildpacksPipelineDSL(LinkedHashMap<String, Object> c, PrintStream ps, EnvActionImpl jenkinsEnv)
                throws Exception {
            
            this.ps = ps;
            this.jenkinsEnv = jenkinsEnv;

            extractParameters(c);

        }

        public String getBuilder() {
            return this.builder;
        }

        public String getPath() {
            return this.path;
        }

        public String getImageName() {
            return this.imageName;
        }

        public Path getEnvFile() {
            return this.envFile;
        }

        public Map<String, String> getEnv() {
            return this.env;
        }

        public ArrayList<String> getBuildpack() {
            return this.buildpack;
        }

        public void setBuilder(String b) {
            this.builder = b;
        }

        public void setPath(String p) {
            this.path = p;
        }

        public void setEnvFile(Path i) {
            this.envFile = i;
        }

        public void setImageName(String i) {
            this.imageName = i;
        }

        public void setEnv(Map<String, String> e) {
            this.env = e;
        }

        public void setBuildpack(ArrayList<String> b) {
            this.buildpack = b;
        }

        /**
         * This method returns the CNB Global variable from global configuration console
         * TODO: it does not have a function at the moment, added as an example, a guide for future global configurations
         * 
         * @return String
         */
        public String getGlobalConfVariable(){
            return conf.getCnbGlobalVariable();
        }

        /**
         * This method returns the Jenkins's current workspace
         * 
         * @return String
         */
        public String getJenkinsWorkspace() {
            return this.jenkinsEnv.getProperty("WORKSPACE");
        }

        /**
         * This method extracts dsl parameters to local variables
         * 
         * @param c
         * @throws Exception
         */
        @SuppressWarnings("unchecked")
        public void extractParameters(LinkedHashMap<String, Object> c) throws Exception {

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
                        setPath(Paths.get(getJenkinsWorkspace(), it.getValue().toString()).toString());
                        break;
                    case "env":
                        if (it.getValue() instanceof ArrayList)
                            extractEnvironmentVariables((ArrayList<String>) it.getValue());
                        else {
                            ArrayList<String> al = new ArrayList<String>();
                            al.add(it.getValue().toString());
                            extractEnvironmentVariables(al);
                        }
                        break;
                    case "envFile":
                        setEnvFile(Paths.get(getJenkinsWorkspace(), it.getValue().toString()));
                        extractEnvironmentVariablesFromFile(getEnvFile());
                        break;
                    case "buildpack":
                        if (it.getValue() instanceof ArrayList)
                            extractBuildpack((ArrayList<String>) it.getValue());
                        else {
                            ArrayList<String> al = new ArrayList<String>();
                            al.add(it.getValue().toString());
                            extractBuildpack(al);
                        }
                        break;
                    default:
                        break;
                }

        }

        /**
         * This method extracts buildpack parameters to local buildpack variable (TODO: it will connect to java-buildpack-client)
         * 
         * @param al
         * @throws Exception
         */
        public void extractBuildpack(ArrayList<String> al) throws Exception {
            ArrayList<String> buildpacks = new ArrayList<String>();

            int i = 0;
            for (Object bp : al) {
                boolean isBuildpackFormat = false;
                String b = bp.toString();
                if (b.isEmpty())
                    throw new Exception(String.format("element %d in buildpack variable is empty", i));
                else {
                    String[] regex = new String[]{
                        "http(s)?://(.*)*/(.*)+", 
                        "file://(.*)*/(.*)+",
                        "docker://(.*)*/(.*)+((:.*){1}(⏐@(.*))?)?", 
                        "urn:cnb:builder((:.*){1}⏐(@(.*){1})?)?",
                        "urn:cnb:registry((:.*){1}⏐(@(.*){1})?)?"
                    };

                    for (String r : regex) {

                        Pattern pattern = Pattern.compile(r, Pattern.CASE_INSENSITIVE);
                        Matcher matcher = pattern.matcher(b);
                        if(matcher.find()){
                            System.out.println(b);
                            isBuildpackFormat = true; 
                            break;
                        }

                    }

                    if(!isBuildpackFormat){
                        File f = new File(Paths.get(getJenkinsWorkspace(), b).toString());
                        if (!f.exists()) 
                            throw new Exception(String.format("element %d in buildpack variable does not conform to formats. See: https://buildpacks.io/docs/app-developer-guide/specific-buildpacks/", i));
                    }

                    buildpacks.add(b);
                    
                }
                i++;
            }

            setBuildpack(buildpacks);
            //buildpacks.forEach(key -> System.out.println(key));
        }

        /**
         * This method checks the env variables used during the build and extracts them to the class instance's env variable
         * 
         * @param al
         * @throws Exception
         */
        public void extractEnvironmentVariables(ArrayList<String> al) throws Exception {
            Map<String, String> envs = new HashMap<>();

            int i = 0;
            for (Object env : al) {
                String e = env.toString();
                String[] s = e.split("=", 2);
                if (e.isEmpty())
                    throw new Exception(String.format("element %d in env variable is empty", i));
                else if (s.length == 2) {

                    if (s[0].isEmpty() || s[1].isEmpty())
                        throw new Exception(String.format(
                                "Error detected in '%s'(element %d in env variable) environment variable definition",
                                env, i));

                    envs.put(s[0], s[1]);

                } else if (s.length == 1) {

                    if (s[0].isEmpty())
                        throw new Exception(String.format(
                                "Error detected in '%s'(element %d in env variable) environment variable definition",
                                env, i));

                    if (System.getenv(s[0]) == null)
                        throw new Exception(String
                                .format("The env variable '%s' is not found in system environment variables", env));
                    else if (System.getenv(s[0]).isEmpty())
                        throw new Exception(String
                                .format("The env variable '%s' exists in environment variables but it's empty", i));
                    envs.put(s[0], System.getenv(s[0]));

                }
                i++;
            }
            setEnv(envs);
            // envs.forEach((key, value) -> System.out.println(key + ":" + value));
        }

        /**
         * This method takes the file where the envs are located as a parameter 
         * and extracts the env variables in that file to the class instance's env variable
         * 
         * @param p
         * @throws Exception
         */
        public void extractEnvironmentVariablesFromFile(Path p) throws Exception {

            BufferedReader br = Files.newBufferedReader(p, StandardCharsets.UTF_8);

            ArrayList<String> al = new ArrayList<>();

            String row;
            while ((row = br.readLine()) != null)
                al.add(row);
            br.close();

            extractEnvironmentVariables(al);
        }

        /**
         * This method checks the minimum parameters for the buildpacks DSL to run
         * 
         * @throws Exception
         */
        public void checkParameters() throws Exception {

            if (getPath().isEmpty())
                throw new Exception("The variable 'path' cannot be empty.");
            if (getBuilder().isEmpty())
                throw new Exception("The variable 'builder' cannot be empty.");
            if (getImageName().isEmpty())
                throw new Exception("The variable 'imageName' cannot be empty.");

        }

        /**
         * This method runs the build phase with previously given parameters
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

            if (this.env.size() > 0)
                bpb = bpb.withEnv(this.env);

            bpb.build(logger);

            return bpb;
        }

    }

    @Extension
    public static class BuildpacksLogger implements LogReader {

        private PrintStream ps;

        public BuildpacksLogger() {
        }

        public BuildpacksLogger(PrintStream ps) {
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
