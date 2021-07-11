package io.jenkins.plugins.lifecycle.api;

public class Api {
    
    Api(){}

    public static Version MustParse(String v) {
        Version version = new Version();
        try {
            version = Version.NewVersion(v);          
        } catch (Exception e) {
            System.out.println(e.getMessage());  
        }
        return version;
    }
}
