package com.cdac.statewide.hiscommon.service.service;

import com.cdac.statewide.hiscommon.service.dto.responsedto.CountryResponseDTO;
import com.cdac.statewide.hiscommon.service.dto.responsedto.DistrictResponseDTO;
import com.cdac.statewide.hiscommon.service.dto.responsedto.StateResponseDTO;

import java.util.List;

public interface LocationService {
    List<CountryResponseDTO> getCountries();
    List<StateResponseDTO> getStates(String countryId);
    List<DistrictResponseDTO> getDistricts(String stateCode);
}
