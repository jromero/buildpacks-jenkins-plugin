package io.jenkins.plugins.cmd;

class LifecycleExitError extends Error {}

public class Exit {

    public final static int CodeFailed = 1; // CodeFailed indicates generic lifecycle error
	// 2: reserved
	public final static int CodeInvalidArgs = 3;
	// 4: CodeInvalidEnv
	// 5: CodeNotFound
	// 9: CodeFailedUpdate

	// API errors
	public final static int CodeIncompatiblePlatformAPI  = 11;
	public final static int CodeIncompatibleBuildpackAPI = 12;

    Exit(){}
    
    public static ErrorFail FailErrCode(Error err,int code, String[] action)  {
        return new ErrorFail(err, code, action);
    }

    public static ErrorFail FailErrCode(Error err, int code, String action)  {
        return new ErrorFail(err, code, action);
    }
}
