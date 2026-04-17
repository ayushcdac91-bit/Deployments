package com.statewide.login.requestdto;

import lombok.Data;

@Data
public class ForgotPasswordRequest {
    private String varUserName;
    private String varEmailId;
    private String varMobileNumber;
    private String otp;
    private String varNewPassword;
    private String varConfirmPassword;

}
