package com.cdac.statewide.hiscommon.service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.cdac.statewide.hiscommon.service.dto.MasterResponse;
import com.cdac.statewide.hiscommon.service.dto.requestdto.UserRequestDTO;
import com.cdac.statewide.hiscommon.service.dto.responsedto.InstituteResponseDTO;
import com.cdac.statewide.hiscommon.service.dto.responsedto.PatientCategoryResponseDTO;
import com.cdac.statewide.hiscommon.service.dto.responsedto.ReferDepartmentResponseDTO;
import com.cdac.statewide.hiscommon.service.service.EssentialMasterDataService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Created By Gaurav Kumar
 * This controller is responsible for fetching the Patient Category,Gender List,
 * Document Type(ID),PaymentMode(Fee),Institute List,Visiting Department/Unit
 * ,Refering Department,Age Type
 */

@RestController
@RequestMapping("/his/common-service")
@Slf4j
public class EssentialMasterDataController {

    private final EssentialMasterDataService essentialMasterDataService;

    public EssentialMasterDataController(EssentialMasterDataService essentialMasterDataService) {
        this.essentialMasterDataService = essentialMasterDataService;
    }

    /**
     * @return ResponseEntity containing the gender list or an error message if
     *         something goes wrong.
     */
    @PostMapping("/getGender")
    public ResponseEntity<Map<String, Object>> getGenderList() {
        log.info("Get Gender API called");
        try {
            Map<String, Object> response = essentialMasterDataService.getGenderList();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Get Gender API failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error",
                            "Something went wrong during Fetching Gender List ",
                            "message",
                            e.getMessage()));
        }
    }

    /**
     * Method responsible for handling requests related to document types.
     * It fetches the document type ID or any other ID based on the provided mode.
     * 
     * @return ResponseEntity with the list of document id or any other id or error
     *         details.
     */
    @PostMapping("/getDocumentType")
    public ResponseEntity<Map<String, Object>> getDocumentType() {
        log.info("Get Document Type API called");
        try {
            Map<String, Object> response = essentialMasterDataService.getDocumentType();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Get Document Type API failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Something went wrong during Fetching Document Type Id", "message",
                            e.getMessage()));
        }

    }

    /**
     * @param request The userRequestDto contains the hospitalCode
     * @return ResponseEntity with either the Payment Mode List
     *         or error details if no data is found.
     */
    @PostMapping("/getPaymentMode")
    public ResponseEntity<Map<String, Object>> getPaymentModeList(@RequestBody UserRequestDTO userRequestDto) {
        log.info("Get PaymentMode List API called");
        try {
            Map<String, Object> response = essentialMasterDataService.getPaymentModeList(userRequestDto);

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("Get PaymentMode List API failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error",
                            "Something went wrong during Fetching Payment(Fee) Mode  List ",
                            "message",
                            e.getMessage()));
        }
    }

    /**
     * 
     * @param request The requestdto contains the hospitalCode, seatId, .
     * @return ResponseEntity with either the list of departments (Visiting
     *         Department or Visiting Department Unit)
     *         or error details if no data is found.
     */
    @PostMapping("/getDepartment")
    ResponseEntity<Map<String, Object>> getDepartment(@Valid @RequestBody UserRequestDTO userRequestDto) {
        log.info("Get Visiting Department API called | hospitalCode={} seatId={}",
                userRequestDto.getHospitalCode(),
                userRequestDto.getSeatId());
        try {
            Map<String, Object> response = essentialMasterDataService.getDepartment(userRequestDto);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Get Visiting Department  API failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error",
                            "Something went wrong during Fetching Visiting Department List ",
                            "message",
                            e.getMessage()));
        }

    }

    @PostMapping("/getDepartmentUnit")
    ResponseEntity<Map<String, Object>> getDepartmentUnit(@Valid @RequestBody UserRequestDTO userRequestDto) {
        log.info("Get Visiting Department Unit API called | hospitalCode={} seatId={}",
                userRequestDto.getHospitalCode(),
                userRequestDto.getSeatId());
        try {
            Map<String, Object> response = essentialMasterDataService.getDepartmentUnit(userRequestDto);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Get Visiting Department Unit API failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error",
                    "Something went wrong during Fetching Visiting Department Unit List ",
                    "message",
                    e.getMessage()));
        }
    }

    /*
     * This Method Returns AgeType
     * "Years": "Yr",
     * "Months": "Mth",
     * "Weeks": "Wk",
     * "Days": "D"
     */
    @PostMapping("/getAgeType")
    public ResponseEntity<Map<String, Object>> getAgeType() {

        Map<String, Object> response = essentialMasterDataService.getAgeType();

        return ResponseEntity.ok(response);
    }

    /**
     * @param request Parameter userRequestDto contains the user_id,seat_id,
     * @return ResponseEntity with either the list of Patient Category
     *         or error details if no data is found.
     */
    @PostMapping("/getPatientCategory")
    public ResponseEntity<MasterResponse<List<PatientCategoryResponseDTO>>> getPatientCategory(
            @RequestBody UserRequestDTO userRequestDto) {
        log.info("GetPatientCategory API called | hospitalCode={} seatId={}",
                userRequestDto.getHospitalCode(),
                userRequestDto.getSeatId());
        try {
            MasterResponse<List<PatientCategoryResponseDTO>> response = essentialMasterDataService
                    .getPatientCategory(userRequestDto);
            log.info("GetPatientCategory API | status={}", response.isSuccess());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("GetPatientCategory API failed", e);
            MasterResponse<List<PatientCategoryResponseDTO>> errorResponse = new MasterResponse<>(
                    false,
                    "Something went wrong during fetching patient category List: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * @param request Parameter userRequestDto contains the hospitalCode
     * 
     * @return ResponseEntity containing the Institute List or an error message if
     *         something goes wrong.
     */
    @PostMapping("/getInstitute")
    public ResponseEntity<MasterResponse<List<InstituteResponseDTO>>> getInstitute(
            @RequestBody UserRequestDTO userRequestDto) {
        log.info("Get Institute List API called");
        try {
            MasterResponse<List<InstituteResponseDTO>> response = essentialMasterDataService
                    .getInstitute(userRequestDto);
            log.info("Get Institute API | status={}", response.isSuccess());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Get Institute  List API failed", e);
            MasterResponse<List<InstituteResponseDTO>> errorResponse = new MasterResponse<>(
                    false,
                    "Something went wrong during fetching Institute List: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     *
     * 
     * @return ResponseEntity containing the Refer Department List or an error
     *         message if
     *         something goes wrong.
     */
    @PostMapping("/getReferDepartment")
    public MasterResponse<List<ReferDepartmentResponseDTO>> getReferDepartment() {
        log.info("Get Refer Department List API called");
        try {
            List<ReferDepartmentResponseDTO> response = essentialMasterDataService
                    .getReferDepartment();
            log.info("Get Refer Department List API completed | count={}", response.size());

            if (response == null || response.isEmpty()) {
                return new MasterResponse<>(
                        false,
                        "No Refer Department Found",
                        HttpStatus.NO_CONTENT.value(),
                        null);
            }
            return new MasterResponse<>(true, "Refer Department Fetched Successfully", HttpStatus.OK.value(), response);
        } catch (Exception e) {
            log.error("Get Refer Department List API failed", e);

            return new MasterResponse<>(
                    false,
                    "Unable to fetch Refer Department List. Please try again later." + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    null);
        }
    }

}
