package com.statewide.login.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordVO {

    private String varUserId;
    private String varHospitalCode;
    private String varUserSeatId;
    private String varNewPassword;
    private String varOldPassword;
    private String varUserName;
    private String varLoggedIn;

}
