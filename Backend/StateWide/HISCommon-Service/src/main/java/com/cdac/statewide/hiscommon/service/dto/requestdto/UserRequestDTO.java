package com.cdac.statewide.hiscommon.service.dto.requestdto;

import lombok.Data;

@Data
public class UserRequestDTO {

    private String hospitalCode;
    private String seatId;
    private String ipAddress;
    private String moduleId;
    private String unitType;
    private String strCrNo;
    private String strRosterType;
    private String strMode;

}
