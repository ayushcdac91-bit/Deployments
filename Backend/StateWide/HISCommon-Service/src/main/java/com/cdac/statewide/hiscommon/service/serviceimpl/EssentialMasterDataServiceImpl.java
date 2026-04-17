package com.cdac.statewide.hiscommon.service.serviceimpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.cdac.statewide.hiscommon.service.config.EssentialCommonDaoConfig;
import com.cdac.statewide.hiscommon.service.dto.MasterResponse;
import com.cdac.statewide.hiscommon.service.dto.requestdto.UserRequestDTO;
import com.cdac.statewide.hiscommon.service.dto.responsedto.InstituteResponseDTO;
import com.cdac.statewide.hiscommon.service.dto.responsedto.PatientCategoryResponseDTO;
import com.cdac.statewide.hiscommon.service.dto.responsedto.ReferDepartmentResponseDTO;
import com.cdac.statewide.hiscommon.service.entity.UserVO;
import com.cdac.statewide.hiscommon.service.repository.EssentialMasterDataRepository;
import com.cdac.statewide.hiscommon.service.service.EssentialMasterDataService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EssentialMasterDataServiceImpl implements EssentialMasterDataService {

    private final EssentialMasterDataRepository essentialMasterDataRepository;

    public EssentialMasterDataServiceImpl(EssentialMasterDataRepository essentialMasterDataRepository) {
        this.essentialMasterDataRepository = essentialMasterDataRepository;
    }

    public MasterResponse<List<PatientCategoryResponseDTO>> getPatientCategory(UserRequestDTO userRequestDto) {
        String modeVal = "1";
        List<PatientCategoryResponseDTO> patientCategory = essentialMasterDataRepository.getPatientCategory(
                userRequestDto,
                modeVal,
                EssentialCommonDaoConfig.NEW_REGISTRATION_TARIFF_ID, EssentialCommonDaoConfig.MODULE_ID_REGISTRATION);
        if (patientCategory == null || patientCategory.isEmpty()) {
            log.warn("No patient category data found");
            return new MasterResponse<>(false, "Patient Category data not found.", HttpStatus.NOT_FOUND.value(), null);
        } else {
            log.info("Patient category fetched successfully | count={}",
                    patientCategory.size());
            return new MasterResponse<>(true, "Patient Category list fetched successfully.", HttpStatus.OK.value(),
                    patientCategory);

        }

    }

    public Map<String, Object> getGenderList() {
        String modeVal = "1";
        Map<String, Object> response = new HashMap<>();
        List genderList = null;
        genderList = essentialMasterDataRepository.getGender(modeVal);
        if (genderList == null || genderList.isEmpty()) {
            log.warn("No Gender  List found.");
            response.put("message", "No Gender  List found.");
            response.put("status", HttpStatus.NOT_FOUND);
        } else {
            log.info("Gender List fetched successfully | count={}",
                    genderList.size());
            response.put("GenderList", genderList);
            response.put("status", HttpStatus.OK.value());
        }
        return response;
    }

    public Map<String, Object> getDocumentType() {
        String pMode = "1";
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> listDocumentTypeId = null;
        listDocumentTypeId = essentialMasterDataRepository.getDocumentTypeRepo(pMode);
        if (listDocumentTypeId == null || listDocumentTypeId.isEmpty()) {
            log.warn("No DocumentType List found");
            response.put("message", "No DocumentType List found .");
            response.put("status", HttpStatus.NOT_FOUND);
        } else {
            log.info(" DocumentType List fetched successfully | count={}",
                    listDocumentTypeId.size());
            response.put("DocumentType", listDocumentTypeId);
            response.put("status", HttpStatus.OK.value());
        }
        return response;
    }

    public Map<String, Object> getPaymentModeList(UserRequestDTO userRequestDto) {
        Map<String, Object> response = new HashMap<>();
        java.util.List<Map<String, Object>> paymentModeList = null;
        String modeVal = "1";
        paymentModeList = essentialMasterDataRepository.getPaymentModeList(userRequestDto, modeVal);
        if (paymentModeList == null || paymentModeList.isEmpty()) {
            log.warn("No Payment Mode List found.");
            response.put("message", "No Payment Mode List found .");
            response.put("status", HttpStatus.NOT_FOUND);
        } else {
            log.info("Payment Mode List  fetched successfully | count={}",
                    paymentModeList.size());
            response.put("Payment ModeList", paymentModeList);
            response.put("status", HttpStatus.OK.value());
        }
        return response;
    }

    public Map<String, Object> getDepartment(UserRequestDTO userRequestDto) {
        Map<String, Object> response = new HashMap<>();
        UserVO userVO = new UserVO();
        userVO.setHospitalCode(userRequestDto.getHospitalCode());
        userVO.setSeatId(userRequestDto.getSeatId());
        userVO.setModuleId(EssentialCommonDaoConfig.MODULE_ID_REGISTRATION);
        userVO.setIpAddress("0.0.0.0");

        List listDepartment = null;
        String strRosterType = "1";
        String modVal = "1";
        String strCrNo_p = "0";

        listDepartment = essentialMasterDataRepository.getVisitingDepartment(userVO, strRosterType, modVal, strCrNo_p);
        if (listDepartment == null || listDepartment.isEmpty()) {
            log.warn("Visiting Department  list not found .");
            response.put("message", "Visiting Department  List not found .");
            response.put("status", HttpStatus.NOT_FOUND.value());
        } else {
            log.info(" Visiting Department List fetched successfully | count={}",
                    listDepartment.size());
            response.put("Visiting Department", listDepartment);
            response.put("status", HttpStatus.OK.value());
        }

        return response;

    }

    public Map<String, Object> getDepartmentUnit(UserRequestDTO userRequestDto) {
        Map<String, Object> response = new HashMap<>();
        UserVO userVO = new UserVO();
        userVO.setHospitalCode(userRequestDto.getHospitalCode());
        userVO.setSeatId(userRequestDto.getSeatId());
        userVO.setModuleId(EssentialCommonDaoConfig.MODULE_ID_REGISTRATION);
        userVO.setIpAddress("0.0.0.0");
        List listDepartmentUnit = null;
        String strRosterType = "1";
        String modVal = "5";
        String strCrNo_p = "0";

        listDepartmentUnit = essentialMasterDataRepository.getVisitingDepartment(userVO, strRosterType, modVal,
                strCrNo_p);
        if (listDepartmentUnit == null || listDepartmentUnit.isEmpty()) {
            log.warn("No Visiting Department Unit data found | hospitalCode={}",
                    userRequestDto.getHospitalCode());
            response.put("message", "Visiting Department Unit List not found .");
            response.put("status", HttpStatus.NOT_FOUND.value());
        } else {
            log.info(" Visiting Department Unit List fetched successfully | count={}",
                    listDepartmentUnit.size());
            response.put("Visiting Department Unit", listDepartmentUnit);
            response.put("status", HttpStatus.OK.value());
        }

        return response;
    }

    public Map<String, Object> getAgeType() {

        List<String> ageList = Arrays.asList(
                "Years", "Yr",
                "Months", "Mth",
                "Weeks", "Wk",
                "Days", "D");

        Map<String, Object> ageTypeMap = new HashMap<>();

        for (int i = 0; i < ageList.size(); i += 2) {
            ageTypeMap.put(ageList.get(i), ageList.get(i + 1));
        }

        return ageTypeMap;
    }

    public MasterResponse<List<InstituteResponseDTO>> getInstitute(UserRequestDTO userRequestDto) {
        String modeVal = "1";
        List<InstituteResponseDTO> instituteList = essentialMasterDataRepository.getInstitute(userRequestDto,
                modeVal, EssentialCommonDaoConfig.SUPER_USER_HOSPITAL_CODE);
        if (instituteList == null || instituteList.isEmpty()) {
            log.warn("No Institute List data found .");
            return new MasterResponse<>(false, "Institute data not found.", HttpStatus.NOT_FOUND.value(), null);
        } else {
            log.info("Institute List fetched successfully | count={}",
                    instituteList.size());
            return new MasterResponse<>(true, "Institute list fetched successfully.", HttpStatus.OK.value(),
                    instituteList);

        }
    }

    public List<ReferDepartmentResponseDTO> getReferDepartment() {
        String modeVal = "1";
        List<ReferDepartmentResponseDTO> referDepartmentList = essentialMasterDataRepository.getReferDepartment(modeVal,
                EssentialCommonDaoConfig.DEPT_TYPE_CLINICAL_VALUE, EssentialCommonDaoConfig.SUPER_USER_HOSPITAL_CODE);
        if (referDepartmentList == null || referDepartmentList.isEmpty()) {
            log.warn("No Refer Department List data found .");
            return Collections.emptyList();
        } else {
            log.info("ReferDepartment List fetched successfully | count={}",
                    referDepartmentList.size());
            return referDepartmentList;

        }
    }

}
