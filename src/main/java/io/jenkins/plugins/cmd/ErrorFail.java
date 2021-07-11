package io.jenkins.plugins.cmd;

public class ErrorFail {

    Error Err;
	int Code;
	String[] Action;

    public ErrorFail(Error err, int code, String[] action){
        this.Err = err;
        this.Code = code;
        this.Action = action;
    }

    public ErrorFail(Error err, int code, String action){
        this.Err = err;
        this.Code = code;
        this.Action = new String[1];
        this.Action[0] = action;
    }

}
