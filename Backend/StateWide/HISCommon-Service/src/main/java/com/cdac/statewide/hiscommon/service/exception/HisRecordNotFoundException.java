package com.cdac.statewide.hiscommon.service.exception;

public class HisRecordNotFoundException extends RuntimeException {

    public HisRecordNotFoundException() {
        super("Record Not Found");
    }

    public HisRecordNotFoundException(String message) {
        super(message);
    }

    public HisRecordNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
