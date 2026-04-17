package com.cdac.statewide.hiscommon.service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HospitalMasterVO {
    private String varHospitalCode;
    private String varHospitalName;
    private String varHospitalAddress1;
    private String varHospitalAddress2;
    private String varCity;
    private String varStateCode;
    private String varStateName;
    private String varPhone;
    private String varFax;
    private String varEmail;
    private String varContactPerson;
    private String varDistrictId;
    private String varDistrictName;
    private String varHL7Code;
    private String varHospitalShortName;
    private String varRemarks;
    private String varIsAssociated;
    private String varHospitalType;
    private String varHospitalCategory;
    private String varOrganizationType;
    private String varBusRouteNo;
    private String varBedCapacity;
    private String varWeekdaysTimings;
    private String varSaturdayTimings;
    private String varLunchBreak;
    private String varPANNo;
    private String varTANNo;
    private String varPinCode;
    private String varUserLicenceAllowed;
    private String varLanguageCode;
    private String varLanguageName;
    private String varLocalLangCode;
    private String varLocalLangName;
    private String varUserId;
    private String varUserSeatId;
    private String varProjectCode;
}
