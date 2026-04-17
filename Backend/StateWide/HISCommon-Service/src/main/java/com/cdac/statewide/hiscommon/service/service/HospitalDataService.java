package com.cdac.statewide.hiscommon.service.service;

import java.util.Map;
import com.cdac.statewide.hiscommon.service.dto.requestdto.HospitalDataRequest;

public interface HospitalDataService {

    public Map<String, Object> getHospitalData(HospitalDataRequest hospitalDataRequest);
}
