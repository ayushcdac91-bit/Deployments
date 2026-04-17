package com.cdac.statewide.hiscommon.service.exception;

public class HISException extends RuntimeException {

    private static final long serialVersionUID = 01L;

    protected String errorId = "0100";

    public HISException() {
        super("HIS Exception");
    }

    public HISException(String strMsg) {
        super(strMsg);
    }

    public String getErrorID() {
        return this.errorId;
    }

}
