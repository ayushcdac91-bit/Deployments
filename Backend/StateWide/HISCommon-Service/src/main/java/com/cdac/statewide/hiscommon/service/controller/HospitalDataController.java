package com.cdac.statewide.hiscommon.service.controller;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.cdac.statewide.hiscommon.service.dto.requestdto.HospitalDataRequest;
import com.cdac.statewide.hiscommon.service.service.HospitalDataService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Gaurav Kumar
 * 
 * This controller is responsible for fetching hospital details.
 * 
 * @return ResponseEntity with the list of hospital data or error details.
 */
@RestController
@RequestMapping("/his/common-service")
@Slf4j
public class HospitalDataController {
    private final HospitalDataService hospitalDataService;

    public HospitalDataController(HospitalDataService hospitalDataService) {
        this.hospitalDataService = hospitalDataService;
    }

    @PostMapping("/gethospital")
    public ResponseEntity<Map<String, Object>> getHospitalDetails(
            @Valid @RequestBody HospitalDataRequest hospitalDataRequest)
            throws Exception {
        log.info("Get Hospital Data API called ");
        try {
            Map<String, Object> response = hospitalDataService.getHospitalData(hospitalDataRequest);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("Get Hospital Data API failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Something went wrong during Fetching Hospital Data", "message",
                            e.getMessage()));
        }
    }

}
