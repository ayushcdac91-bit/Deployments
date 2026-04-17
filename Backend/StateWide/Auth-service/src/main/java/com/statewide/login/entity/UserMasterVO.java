package com.statewide.login.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMasterVO {

    private String varUserId;
    private String varUserName;
    private String varPassword;
    private String varUserSeatId;
    private String varEmpNo;
    private String varHospitalCode;
    private String varUserLevel;
    private String varUsrName;
    private String varDesignation;
    private String varMobileNumber;
    private String varEmailId;
    private String varDistrictId;
    private String varDistrictName;
    private String varQuestionId;
    private String varHintAnswer;
    private String varLock;
    private String varChangePasswordDate;

    private String varMenuId;
    private String varDefaultMenuURL;
    private String varDefaultMenuModule;
    private String varDefaultMenuName;
    private Long varIsAutoRefresh;
    private String varOldPassword;
    private String varIPAddress;

    private String jwtToken;
    private String varEffectDate;
    private String varEntryDate;
    private String varExpiryDate;
    private String varLastModifyDate;
    private String varIsValid;
    private String varLastModifySeatId;
    private String varSeatId;
    private String varSwapcardNumber;
    private String varUserType;
    private String varUserTypeId;
    private String varStatusCode;

    // Sysdate and time
    private String varCurrentYear;
    private String varCurrentMonth;
    private String varCurrentDate;
    private String varCurrentHour;
    private String varCurrentMinute;
    private String varCurrentSecond;

}
