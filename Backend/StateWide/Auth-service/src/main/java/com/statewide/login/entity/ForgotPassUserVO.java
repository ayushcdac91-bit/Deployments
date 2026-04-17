package com.statewide.login.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPassUserVO {

    private Long varUserId;
    private String varUserName;
    private String varPassword;
    private Long varHospitalCode;
    private Long varUserSeatId;
    private String varEmailId;
    private String varMobileNumber;

    // Additional fields for procedure
    private String varOldPassword;
    private String varEmpNo;
    private String varQuestionId;
    private String varHintAnswer;
    private String varMenuId;
    private String varNewPassword;
}
