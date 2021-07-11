package io.jenkins.plugins.lifecycle.priv;

public class Priv {
    public static boolean IsPrivileged() {
        return os.Getuid() == 0
    }
}
