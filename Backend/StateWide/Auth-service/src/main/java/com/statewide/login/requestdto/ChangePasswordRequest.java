package com.statewide.login.requestdto;

import lombok.Data;

@Data
public class ChangePasswordRequest {

    private String varOldPassword;
    private String varNewPassword;
    private String varConfirmPassword;
}
