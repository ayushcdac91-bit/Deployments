package com.cdac.statewide.hiscommon.service.dto.responsedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountryResponseDTO {
    private String GSTR_COUNTRY_CODE;
    private String GSTR_COUNTRY_NAME;
}