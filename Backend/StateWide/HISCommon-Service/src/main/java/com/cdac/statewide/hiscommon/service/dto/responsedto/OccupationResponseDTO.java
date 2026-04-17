package com.cdac.statewide.hiscommon.service.dto.responsedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OccupationResponseDTO {
    private String GNUM_OCCUPATION_CODE;
    private String GSTR_OCCUPATION_NAME;
}
