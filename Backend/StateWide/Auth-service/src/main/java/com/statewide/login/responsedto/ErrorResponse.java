package com.statewide.login.responsedto;

import java.time.Instant;

public record ErrorResponse(String error,
        String message,
        Instant timestamp) {

}
