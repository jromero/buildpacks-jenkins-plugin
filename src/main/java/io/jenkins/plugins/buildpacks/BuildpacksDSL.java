package io.jenkins.plugins.buildpacks;

import hudson.Extension;
import io.jenkins.plugins.pipelinedsl.PipelineDSLGlobal;
import org.jenkinsci.plugins.scriptsecurity.sandbox.whitelists.ProxyWhitelist;
import org.jenkinsci.plugins.scriptsecurity.sandbox.whitelists.StaticWhitelist;

import java.io.IOException;

@Extension
public class BuildpacksDSL extends PipelineDSLGlobal {

    @Override
    public String getFunctionName() {
        return "buildpacks";
    }

    @Extension
    public static class MiscWhitelist extends ProxyWhitelist {
        public MiscWhitelist() throws IOException {
            super(new StaticWhitelist(
                    "method java.util.Map$Entry getKey",
                    "method java.util.Map$Entry getValue"
            ));
        }
    }

}
