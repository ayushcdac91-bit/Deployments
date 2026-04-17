package com.cdac.statewide.hiscommon.service.serviceimpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.cdac.statewide.hiscommon.service.entity.HospitalMasterVO;
import com.cdac.statewide.hiscommon.service.mapper.HospitalDataMapper;
import com.cdac.statewide.hiscommon.service.repository.HospitalDataRepository;
import com.cdac.statewide.hiscommon.service.service.HospitalDataService;

import lombok.extern.slf4j.Slf4j;

import com.cdac.statewide.hiscommon.service.dto.requestdto.HospitalDataRequest;

@Service
@Slf4j
public class HospitalDataServiceImpl implements HospitalDataService {
    private final HospitalDataRepository hospitalDataRepository;
    private final HospitalDataMapper hospitalDataMapper;

    public HospitalDataServiceImpl(HospitalDataRepository hospitalDataRepository,
            HospitalDataMapper hospitalDataMapper) {
        this.hospitalDataRepository = hospitalDataRepository;
        this.hospitalDataMapper = hospitalDataMapper;
    }

    public Map<String, Object> getHospitalData(HospitalDataRequest hospitalDataRequest) {
        Map<String, Object> response = new HashMap<>();
        HospitalMasterVO voHospital = new HospitalMasterVO();
        voHospital.setVarHospitalCode(hospitalDataRequest.getVarHospitalCode());
        voHospital.setVarUserId(hospitalDataRequest.getVarUserId());
        voHospital.setVarUserSeatId(hospitalDataRequest.getVarUserSeatId());
        List<HospitalMasterVO> hospitals = hospitalDataRepository.getHospitalDetail("1", voHospital);

        if (hospitals == null || hospitals.isEmpty()) {
            log.warn("No Hospital data found | hospitalCode={}",
                    voHospital.getVarHospitalCode());
            response.put("message", "No hospital data found");
            response.put("status", HttpStatus.NOT_FOUND.value());
            return response;
        }
        log.info("Hospital data fetched successfully | count={}", hospitals.size());
        HospitalMasterVO hospitalMasterData = hospitals.get(0);
        response.put("hospitalData", hospitalDataMapper.toHospitalDataResponse(hospitalMasterData));
        response.put("message", "Hospital data fetched successfully");
        response.put("status", HttpStatus.OK.value());
        return response;
    }

}
