package com.cdac.statewide.hiscommon.service.dto.responsedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Marital Status API Response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaritalStatusResponseDTO {
    private String GNUM_MARITAL_STATUS_CODE;
    private String GSTR_MARITAL_STATUS;
}
