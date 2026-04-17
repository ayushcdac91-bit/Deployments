package com.cdac.statewide.hiscommon.service.dto.responsedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientCategoryResponseDTO {

    private String GNUM_PATIENT_CAT_CODE;
    private String GSTR_PATIENT_CAT_NAME;

}
