package io.jenkins.plugins.lifecycle;

import io.jenkins.plugins.cmd.*;

class DetectArgs {

    String buildpacksDir;
	String appDir;
	String layersDir;
	String platformDir;
	String orderPath;

    Platform platform;

    public DetectArgs(){}

}

class DetectCmd extends DetectArgs{

    DetectArgs detectArgs;

    String groupPath = "";
	String planPath = "";

    public DetectCmd(){}

    public ErrorFail Args(int nargs, String[] args) {
        if (nargs != 0){
            return Exit.FailErrCode(new Error("received unexpected arguments"), Exit.CodeInvalidArgs, "parse arguments");
        }
    
        if (this.groupPath == Exit.PlaceholderGroupPath) {
            this.groupPath = Exit.DefaultGroupPath(this.platform.API(), this.layersDir);
        }
    
        if (this.planPath == Exit.PlaceholderPlanPath) {
            this.planPath = Exit.DefaultPlanPath(this.platform.API(), this.layersDir);
        }
    
        if (this.orderPath == Exit.PlaceholderOrderPath) {
            this.orderPath = Exit.DefaultOrderPath(this.platform.API(), this.layersDir);
        }
    
        return null;
    }

}

public class Detect {
    
    Detect(){

    }

}