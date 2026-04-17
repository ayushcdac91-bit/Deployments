package com.statewide.login.exception;

public class HISDataAccessException extends HISException {

    private static final long serialVersionUID = 0102L;

    public HISDataAccessException() {
        super("HIS Data Access Exception");
        this.errorId = "0102";
    }

    public HISDataAccessException(String strMsg) {
        super("HIS Data Access Exception::" + strMsg);
    }
}
