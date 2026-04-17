package com.cdac.statewide.hiscommon.service.mapper;

import com.cdac.statewide.hiscommon.service.entity.HospitalMasterVO;
import com.cdac.statewide.hiscommon.service.dto.responsedto.HospitalDataResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HospitalDataMapper {
    HospitalDataResponseDTO toHospitalDataResponse(HospitalMasterVO hospitalMasterVO);
}
