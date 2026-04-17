package com.statewide.login.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginLogVO {

    // Successful Log
    private String varUserId;
    private String varUserLoginDate;
    private String varUserLoginTime;
    private String varUserLogoutDate;
    private String varUserLogoutTime;
    private String varHospitalCode;
    private String varSeatId;
    private String varIPAddress;
    private String varLoginStatus;
    private String varCounterNumber;
    private LocalDateTime dt;

    // Unsuccessful Log
    private String varUserName;
    private String varEntryDate;
    private String varIsValid;

    private String varUnsuccessfulCount;

    private String varMacAddress;
    private String varClientSSOTicketId;
    private String varAppHost;
    private String varUserSeatId;
}
