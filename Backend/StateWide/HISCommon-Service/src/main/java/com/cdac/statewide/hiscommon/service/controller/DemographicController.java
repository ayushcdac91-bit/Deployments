package com.cdac.statewide.hiscommon.service.controller;

import com.cdac.statewide.hiscommon.service.dto.MasterResponse;
import com.cdac.statewide.hiscommon.service.dto.responsedto.CasteResponseDTO;
import com.cdac.statewide.hiscommon.service.dto.responsedto.MaritalStatusResponseDTO;
import com.cdac.statewide.hiscommon.service.dto.responsedto.OccupationResponseDTO;
import com.cdac.statewide.hiscommon.service.dto.responsedto.ReligionResponseDTO;
import com.cdac.statewide.hiscommon.service.service.DemographicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for fetching demographic master data such as
 * marital status, religion, caste, and occupation.
 *
 * @author Priyanka Thakur
 */
@Slf4j
@RestController
@RequestMapping("/his/common-service")
public class DemographicController {

    private final DemographicService demographicService;

    public DemographicController(DemographicService demographicService) {
        this.demographicService = demographicService;
    }

    /**
     * API: Get Marital Status List
     * Method: POST
     * Endpoint: /his/common-service/getMarital
     * Description: Fetches all marital statuses from the database.
     */
    @PostMapping("/getMarital")
    public MasterResponse<List<MaritalStatusResponseDTO>> getMaritalStatus() {
        log.info("[DemographicController] Inside getMarital API");

        List<MaritalStatusResponseDTO> list = demographicService.getMaritalStatus();
        if (list == null || list.isEmpty()) {
            return new MasterResponse<>(false, "No Marital Status Found", HttpStatus.NO_CONTENT.value(), null);
        }

        return new MasterResponse<>(true, "Marital Status fetched successfully", HttpStatus.OK.value(), list);
    }

    /**
     * API: Get Religion List
     * Method: POST
     * Endpoint: /his/common-service/getReligion
     * Description: Fetches all religions from the database.
     */
    @PostMapping("/getReligion")
    public MasterResponse<List<ReligionResponseDTO>> getReligion() {
        log.info("[DemographicController] Inside getReligion API");

        List<ReligionResponseDTO> list = demographicService.getReligion();
        if (list == null || list.isEmpty()) {
            return new MasterResponse<>(false, "No Religion Found", HttpStatus.NO_CONTENT.value(), null);
        }

        return new MasterResponse<>(true, "Religion list fetched successfully", HttpStatus.OK.value(), list);
    }

    /**
     * API: Get Patient Caste List
     * Method: POST
     * Endpoint: /his/common-service/getPatientCaste
     * Description: Fetches all patient castes from the database.
     */
    @PostMapping("/getPatientCaste")
    public MasterResponse<List<CasteResponseDTO>> getPatientCaste() {
        log.info("[DemographicController] Inside getPatientCaste API");

        List<CasteResponseDTO> list = demographicService.getPatientCaste();
        if (list == null || list.isEmpty()) {
            return new MasterResponse<>(false, "No Patient Caste Found", HttpStatus.NO_CONTENT.value(), null);
        }

        return new MasterResponse<>(true, "Patient Caste list fetched successfully", HttpStatus.OK.value(), list);
    }

    /**
     * API: Get Patient Occupation List
     * Method: POST
     * Endpoint: /his/common-service/getPatientOccupation
     * Description: Fetches all patient occupations from the database.
     */
    @PostMapping("/getPatientOccupation")
    public MasterResponse<List<OccupationResponseDTO>> getPatientOccupation() {
        log.info("[DemographicController] Inside getPatientOccupation API");

        List<OccupationResponseDTO> list = demographicService.getPatientOccupation();
        if (list == null || list.isEmpty()) {
            return new MasterResponse<>(false, "No Patient Occupation Found", HttpStatus.NO_CONTENT.value(), null);
        }

        return new MasterResponse<>(true, "Patient Occupation list fetched successfully", HttpStatus.OK.value(), list);
    }
}
