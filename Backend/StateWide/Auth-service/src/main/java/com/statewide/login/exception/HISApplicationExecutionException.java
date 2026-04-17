package com.statewide.login.exception;

public class HISApplicationExecutionException extends HISException {

    private static final long serialVersionUID = 0101L;

    public HISApplicationExecutionException() {
        super("HIS Application Execution Exception");
        this.errorId = "0101";
    }

    public HISApplicationExecutionException(String strMsg) {
        super("HIS Application Execution Exception::" + strMsg);
    }

}
