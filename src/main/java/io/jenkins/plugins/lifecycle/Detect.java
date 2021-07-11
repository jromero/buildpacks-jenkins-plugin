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
    
        if (this.groupPath == Flags.PlaceholderGroupPath) {
            this.groupPath = Flags.DefaultGroupPath(this.platform.API(), this.layersDir);
        }
    
        if (this.planPath == Flags.PlaceholderPlanPath) {
            this.planPath = Flags.DefaultPlanPath(this.platform.API(), this.layersDir);
        }
    
        if (this.orderPath == Flags.PlaceholderOrderPath) {
            this.orderPath = Flags.DefaultOrderPath(this.platform.API(), this.layersDir);
        }
    
        return null;
    }

    public static Error Privileges() {
        // detector should never be run with privileges
        if (priv.IsPrivileged()) {
            return cmd.FailErr(errors.New("refusing to run as root"), "build")
        }
        return nil
    }

}

public class Detect {
    
    Detect(){

    }

}