package com.cdac.statewide.hiscommon.service.service;

import com.cdac.statewide.hiscommon.service.dto.responsedto.CasteResponseDTO;
import com.cdac.statewide.hiscommon.service.dto.responsedto.MaritalStatusResponseDTO;
import com.cdac.statewide.hiscommon.service.dto.responsedto.OccupationResponseDTO;
import com.cdac.statewide.hiscommon.service.dto.responsedto.ReligionResponseDTO;

import java.util.List;

public interface DemographicService {
    List<MaritalStatusResponseDTO> getMaritalStatus();
    List<ReligionResponseDTO> getReligion();
    List<CasteResponseDTO> getPatientCaste();
    List<OccupationResponseDTO> getPatientOccupation();
}
