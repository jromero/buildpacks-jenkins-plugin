package io.jenkins.plugins.cmd;

public interface Platform {
    String API();
    int CodeFor(LifecycleExitError errType);
    boolean SupportsAssetPackages();
}
