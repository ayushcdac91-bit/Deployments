package com.cdac.statewide.hiscommon.service.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UserVO is the class that specifies getters and setters for all the
 * identifiers
 * which are used for retrieving and inserting user information in the DB
 * tables.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVO {

    private String hospitalCode;
    private String seatId;
    private String ipAddress;
    private String moduleId;

    private String unitType;
    private String strCrNo;
    private String strRosterType;
    private String strMode;

    // Get Menu Data API

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
    private String varCounterName;
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
