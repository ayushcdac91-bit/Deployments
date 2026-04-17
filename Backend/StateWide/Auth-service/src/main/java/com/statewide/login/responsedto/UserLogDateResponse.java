package com.statewide.login.responsedto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserLogDateResponse {
    private String varLoginStatus;
    private String varUserLoginDate;
    private String varUserLoginTime;
    private String varUserLogoutDate;
    private String varUserLogoutTime;
    private String varIPAddress;
    private String varCounterNumber;
    private LocalDateTime dt;

}
