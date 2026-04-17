package com.statewide.login.requestdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeUserDetailsRequest {
    private String varPassword;

    private String varUserName;
    private String varQuestionId;
    private String varHintAnswer;
    private String varOldHintAnswer;
    private String varUserId;
    private String varUserSeatId;

    private String varMobileNumber;
    private String varEmailId;
    private String varMenuId;
    private String varModuleId;
    private String[] varFavMenuId;
}
