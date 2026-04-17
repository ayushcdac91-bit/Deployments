package com.cdac.statewide.hiscommon.service.serviceimpl;

import com.cdac.statewide.hiscommon.service.dto.responsedto.CasteResponseDTO;
import com.cdac.statewide.hiscommon.service.dto.responsedto.MaritalStatusResponseDTO;
import com.cdac.statewide.hiscommon.service.dto.responsedto.OccupationResponseDTO;
import com.cdac.statewide.hiscommon.service.dto.responsedto.ReligionResponseDTO;
import com.cdac.statewide.hiscommon.service.exception.HISDataAccessException;
import com.cdac.statewide.hiscommon.service.exception.HisRecordNotFoundException;
import com.cdac.statewide.hiscommon.service.repository.DemographicRepository;
import com.cdac.statewide.hiscommon.service.service.DemographicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DemographicServiceImpl implements DemographicService {

    private final DemographicRepository demographicRepository;

    public DemographicServiceImpl(DemographicRepository demographicRepository) {
        this.demographicRepository = demographicRepository;
    }

    @Override
    public List<MaritalStatusResponseDTO> getMaritalStatus() {
        log.info("[DemographicServiceImpl] Fetching marital status list...");
        try {
            List<MaritalStatusResponseDTO> list = demographicRepository.getMaritalStatus();
            if (list.isEmpty()) {
                log.info("[DemographicServiceImpl] No marital statuses found.");
            }
            return list;
        } catch (HisRecordNotFoundException | HISDataAccessException e) {
            log.info("[DemographicServiceImpl] Known error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.info("[DemographicServiceImpl] Unexpected error: {}", e.getMessage(), e);
            throw new HISDataAccessException("PAT_500: Unexpected error - " + e.getMessage(), e);
        }
    }

    @Override
    public List<ReligionResponseDTO> getReligion() {
        log.info("[DemographicServiceImpl] Fetching religion list...");
        try {
            List<ReligionResponseDTO> list = demographicRepository.getReligion();
            if (list.isEmpty()) {
                log.info("[DemographicServiceImpl] No religions found.");
            }
            return list;
        } catch (HisRecordNotFoundException | HISDataAccessException e) {
            log.info("[DemographicServiceImpl] Known error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.info("[DemographicServiceImpl] Unexpected error: {}", e.getMessage(), e);
            throw new HISDataAccessException("PAT_500: Unexpected error - " + e.getMessage(), e);
        }
    }

    @Override
    public List<CasteResponseDTO> getPatientCaste() {
        log.info("[DemographicServiceImpl] Fetching patient caste list...");
        try {
            List<CasteResponseDTO> list = demographicRepository.getPatientCaste();
            if (list.isEmpty()) {
                log.info("[DemographicServiceImpl] No patient castes found.");
            }
            return list;
        } catch (HisRecordNotFoundException | HISDataAccessException e) {
            log.info("[DemographicServiceImpl] Known error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.info("[DemographicServiceImpl] Unexpected error: {}", e.getMessage(), e);
            throw new HISDataAccessException("PAT_500: Unexpected error - " + e.getMessage(), e);
        }
    }

    @Override
    public List<OccupationResponseDTO> getPatientOccupation() {
        log.info("[DemographicServiceImpl] Fetching patient occupation list...");
        try {
            List<OccupationResponseDTO> list = demographicRepository.getPatientOccupation();
            if (list.isEmpty()) {
                log.info("[DemographicServiceImpl] No patient occupations found.");
            }
            return list;
        } catch (HisRecordNotFoundException | HISDataAccessException e) {
            log.info("[DemographicServiceImpl] Known error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.info("[DemographicServiceImpl] Unexpected error: {}", e.getMessage(), e);
            throw new HISDataAccessException("PAT_500: Unexpected error - " + e.getMessage(), e);
        }
    }
}
