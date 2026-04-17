package com.cdac.statewide.hiscommon.service.service;

import java.util.List;
import java.util.Map;
import com.cdac.statewide.hiscommon.service.dto.MasterResponse;
import com.cdac.statewide.hiscommon.service.dto.requestdto.UserRequestDTO;
import com.cdac.statewide.hiscommon.service.dto.responsedto.InstituteResponseDTO;
import com.cdac.statewide.hiscommon.service.dto.responsedto.PatientCategoryResponseDTO;
import com.cdac.statewide.hiscommon.service.dto.responsedto.ReferDepartmentResponseDTO;

public interface EssentialMasterDataService {

    public Map<String, Object> getGenderList();

    public Map<String, Object> getDocumentType();

    public Map<String, Object> getPaymentModeList(UserRequestDTO userRequestDto);

    public Map<String, Object> getDepartment(UserRequestDTO userRequestDto);

    public Map<String, Object> getDepartmentUnit(UserRequestDTO userRequestDto);

    public Map<String, Object> getAgeType();

    public MasterResponse<List<PatientCategoryResponseDTO>> getPatientCategory(UserRequestDTO userRequestDto);

    public MasterResponse<List<InstituteResponseDTO>> getInstitute(UserRequestDTO userRequestDto);

    public List<ReferDepartmentResponseDTO> getReferDepartment();

}
