package com.cdac.statewide.hiscommon.service.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import com.cdac.statewide.hiscommon.service.config.EssentialCommonDaoConfig;
import com.cdac.statewide.hiscommon.service.dto.requestdto.UserRequestDTO;
import com.cdac.statewide.hiscommon.service.dto.responsedto.InstituteResponseDTO;
import com.cdac.statewide.hiscommon.service.dto.responsedto.PatientCategoryResponseDTO;
import com.cdac.statewide.hiscommon.service.dto.responsedto.ReferDepartmentResponseDTO;
import com.cdac.statewide.hiscommon.service.entity.UserVO;
import com.cdac.statewide.hiscommon.service.exception.HISDataAccessException;
import com.cdac.statewide.hiscommon.service.utils.HelperMethods;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Repository
@Transactional
@Slf4j
public class EssentialMasterDataRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * @param userRequestDto,modeVal,strTarrifId,moduleId
     * @return List of the patient category .
     */
    public List<PatientCategoryResponseDTO> getPatientCategory(UserRequestDTO userRequestDto, String modeVal,
            String strTarrifId,
            String moduleId) {
        List<PatientCategoryResponseDTO> patientCategories = new ArrayList<>();

        String strProcName = EssentialCommonDaoConfig.PROCEDURE_GET_PATIENT_CAT_COMBO;
        try {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery(
                    strProcName);
            query.registerStoredProcedureParameter("p_modeVal", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_hosp_code", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_tariffid", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_moduleId", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_seatId", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("err", String.class, ParameterMode.OUT);
            query.registerStoredProcedureParameter("resultset", void.class, ParameterMode.REF_CURSOR);

            query.setParameter("p_modeVal", Optional.ofNullable(modeVal).orElse(null));
            query.setParameter("p_hosp_code", Optional.ofNullable(userRequestDto.getHospitalCode()).orElse(null));
            query.setParameter("p_tariffid", Optional.ofNullable(strTarrifId).orElse(null));
            query.setParameter("p_moduleId", Optional.ofNullable(moduleId).orElse(null));
            query.setParameter("p_seatId", Optional.ofNullable(userRequestDto.getSeatId()).orElse(null));
            query.execute();

            String err = (String) query.getOutputParameterValue("err");
            if (err != null && !err.isEmpty()) {
                log.error("Database error  {}", err);
                throw new RuntimeException("Database Error: " + err);
            }
            List<Object[]> resultList = query.getResultList();
            if (resultList != null) {
                for (Object[] row : resultList) {
                    PatientCategoryResponseDTO dto = new PatientCategoryResponseDTO();
                    dto.setGNUM_PATIENT_CAT_CODE(row[0] != null ? row[0].toString() : null);
                    dto.setGSTR_PATIENT_CAT_NAME(row[1] != null ? row[1].toString() : null);
                    patientCategories.add(dto);
                }
            }

        } catch (Exception e) {
            log.error("Database error while fetching patient category", e);
            throw new HISDataAccessException(
                    "PatientCategoryRepository.getPatientCategory() ::executeProcedureByPosition"
                            + ") -> " + e.getMessage());
        }
        return patientCategories;
    }

    /**
     * @param modeVal
     * @return List of Map the Gender List
     */
    public List<Map<String, Object>> getGender(String modeVal) {
        List<Map<String, Object>> genderRecordList = null;

        String strProcName = EssentialCommonDaoConfig.PROCEDURE_GET_GENDER_COMBO;
        try {

            StoredProcedureQuery query = entityManager.createStoredProcedureQuery(
                    strProcName);
            query.registerStoredProcedureParameter("p_modeval", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("err", String.class, ParameterMode.OUT);
            query.registerStoredProcedureParameter("resultset", void.class, ParameterMode.REF_CURSOR);
            query.setParameter("p_modeval", modeVal);
            query.execute();
            String err = (String) query.getOutputParameterValue("err");
            if (err != null && !err.isEmpty()) {
                log.error("Database error  {}", err);
                throw new RuntimeException("Database Error: " + err);
            }
            @SuppressWarnings("unchecked")
            List<Object[]> resultList = query.getResultList();
            String[] columns = { "GenderCode", "GenderName" };
            genderRecordList = HelperMethods.toListWithColumnNames(resultList, columns);
        } catch (Exception e) {
            log.error("Database error while fetching Gender", e);
            throw new HISDataAccessException(
                    "GenderRepository.getGender() ::executeProcedureByPosition"
                            + ") -> " + e.getMessage());
        }
        return genderRecordList;

    }

    /**
     * Retrieves data for document type id
     * 
     * @return List of the document id and Type
     */
    public List<Map<String, Object>> getDocumentTypeRepo(String pMode) {
        List<Map<String, Object>> alRecord = new ArrayList<Map<String, Object>>();
        String strProcName = EssentialCommonDaoConfig.PROCEDURE_GET_DOC_COMBO;
        try {

            StoredProcedureQuery query = entityManager.createStoredProcedureQuery(
                    strProcName);

            query.registerStoredProcedureParameter("p_mode", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("err", String.class, ParameterMode.OUT);
            query.registerStoredProcedureParameter("resultset", void.class, ParameterMode.REF_CURSOR);
            query.setParameter("p_mode", pMode);
            query.execute();

            String err = (String) query.getOutputParameterValue("err");
            if (err != null && !err.isEmpty()) {
                log.error("Database error  {}", err);
                throw new RuntimeException("Database Error: " + err);
            }
            @SuppressWarnings("unchecked")
            List<Object[]> resultList = query.getResultList();

            String[] columns = { "Document_Code", "DOcument_Type" };

            alRecord = HelperMethods.toListWithColumnNames(resultList, columns);

        } catch (Exception e) {
            log.error("Database error while fetching DocumentTypeId", e);
            throw new HISDataAccessException(
                    "DocumentTypeIdRepository.getDepartmentRepo() ::executeProcedureByPosition"
                            + ") -> " + e.getMessage());
        }

        return alRecord;
    }

    /**
     * @param userRequestDto,modeVal,superHospitalCode
     * @return List of the Institute.
     */
    public List<InstituteResponseDTO> getInstitute(UserRequestDTO userRequestDto, String modeVal,
            String superHospitalCode) {
        List<InstituteResponseDTO> instituteList = new ArrayList<>();

        String strProcName = EssentialCommonDaoConfig.PROCEDURE_GET_REF_INSTITUTE_COMBO;
        try {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery(
                    strProcName);
            query.registerStoredProcedureParameter("p_modeVal", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_sup_hosp_code", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_hosp_code", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("err", String.class, ParameterMode.OUT);
            query.registerStoredProcedureParameter("resultset", void.class, ParameterMode.REF_CURSOR);

            query.setParameter("p_modeVal", Optional.ofNullable(modeVal).orElse(null));
            query.setParameter("p_sup_hosp_code", Optional.ofNullable(superHospitalCode).orElse(null));
            query.setParameter("p_hosp_code", Optional.ofNullable(userRequestDto.getHospitalCode()).orElse(null));
            query.execute();

            String err = (String) query.getOutputParameterValue("err");
            if (err != null && !err.isEmpty()) {
                log.error("Database error  {}", err);
                throw new RuntimeException("Database Error: " + err);
            }
            List<Object[]> resultList = query.getResultList();
            if (resultList != null) {
                for (Object[] row : resultList) {
                    InstituteResponseDTO dto = new InstituteResponseDTO();
                    dto.setInstituteCode(row[0] != null ? row[0].toString() : null);
                    dto.setInstituteName(row[1] != null ? row[1].toString() : null);
                    instituteList.add(dto);
                }
            }

        } catch (Exception e) {
            log.error("Database error while fetching Get Institute List", e);
            throw new HISDataAccessException(
                    "PatientCategoryRepository.getInstitute() ::executeProcedureByPosition"
                            + ") -> " + e.getMessage());
        }
        return instituteList;
    }

    /**
     * @param userVO provides user details
     * @return List of the visitingdepartment/visitingdepartmentUnit
     */
    public List<Map<String, Object>> getVisitingDepartment(UserVO userVO, String strRosterType, String modVal,
            String strCrNo_p) {
        List<Map<String, Object>> alRecord = new ArrayList<Map<String, Object>>();
        String strProcName = EssentialCommonDaoConfig.PROCEDURE_GET_DEPT_COMBO;
        try {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery(
                    strProcName);

            query.registerStoredProcedureParameter("p_modeval", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_hcode", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_rostertype", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_seatid", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_ipaddress", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_moduleid", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_crno", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("err", String.class, ParameterMode.OUT);
            query.registerStoredProcedureParameter("resultset", void.class, ParameterMode.REF_CURSOR);

            query.setParameter("p_modeval", modVal);
            query.setParameter("p_hcode", userVO.getHospitalCode());
            query.setParameter("p_rostertype", strRosterType);
            query.setParameter("p_seatid", userVO.getSeatId());
            query.setParameter("p_ipaddress",
                    userVO.getIpAddress() == null ? "" : userVO.getIpAddress());
            query.setParameter("p_moduleid", userVO.getModuleId() == null ? "" : userVO.getModuleId());
            query.setParameter("p_crno", strCrNo_p);
            query.execute();

            String err = (String) query.getOutputParameterValue("err");
            if (err != null && !err.isEmpty()) {
                log.error("Stored procedure error in Get Visiting Department | error={}", err);
                throw new RuntimeException("Database Error: " + err);
            }
            @SuppressWarnings("unchecked")
            List<Object[]> resultList = query.getResultList();

            for (Object[] row : resultList) {

                String deptInfo = (String) row[0];
                String deptName = (String) row[1];
                Map<String, Object> rowData = new HashMap<>();
                rowData.put("deptInfo", deptInfo);
                rowData.put("deptName", deptName);

                alRecord.add(rowData);
            }

            // alRecord = HelperMethodsDAO.toListOfMap(resultList);
            // log.info("resultList----" + resultList);
            // log.info("hospitalcode {}" + userVO.getHospitalCode() + "seatid {}" +
            // userVO.getSeatId() + "moduleid {}"
            // + userVO.getModuleId() + "mode {}" + userVO.getStrMode());
        } catch (Exception e) {
            log.error("Database error while fetching Get Visiting Department", e);
            throw new HISDataAccessException(
                    "DepartmentRepository.getDepartmentRepo() ::executeProcedureByPosition"
                            + ") -> " + e.getMessage());
        }

        return alRecord;
    }

    /**
     * @param modeVal,deptTypeClinicalValue,superHospitalCode,
     * @return List of the Refer Department code and name .
     */
    public List<ReferDepartmentResponseDTO> getReferDepartment(String modeVal, String deptTypeClinicalValue,
            String superHospitalCode) {
        List<ReferDepartmentResponseDTO> referDepartmentList = new ArrayList<>();

        String strProcName = EssentialCommonDaoConfig.PROCEDURE_GET_REF_DEPARTMENT_COMBO;
        try {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery(
                    strProcName);
            query.registerStoredProcedureParameter("p_modeVal", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_hosp_code", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_dept_type_clinical", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("err", String.class, ParameterMode.OUT);
            query.registerStoredProcedureParameter("resultset", void.class, ParameterMode.REF_CURSOR);

            query.setParameter("p_modeVal", Optional.ofNullable(modeVal).orElse(null));
            query.setParameter("p_hosp_code", Optional.ofNullable(superHospitalCode).orElse(null));
            query.setParameter("p_dept_type_clinical", Optional.ofNullable(deptTypeClinicalValue).orElse(null));
            query.execute();

            String err = (String) query.getOutputParameterValue("err");
            if (err != null && !err.isEmpty()) {
                log.error("Stored procedure error in Get Refer Department | error={}", err);
                throw new RuntimeException("Database Error: " + err);
            }
            List<Object[]> resultList = query.getResultList();
            if (resultList != null) {
                for (Object[] row : resultList) {
                    ReferDepartmentResponseDTO dto = new ReferDepartmentResponseDTO();
                    dto.setGNUM_DEPT_CODE(row[0] != null ? row[0].toString() : null);
                    dto.setGSTR_DEPT_NAME(row[1] != null ? row[1].toString() : null);
                    referDepartmentList.add(dto);
                }
            }

        } catch (Exception e) {
            log.error("DB failure in Get Refer Department", e);
            throw new HISDataAccessException(
                    "PatientCategoryRepository.getReferDepartment()  failed", e);
        }
        return referDepartmentList;
    }

    /**
     * @param userRequestDto and modeVal provides user details.
     * @return List of the payment mode List.
     */
    public List<Map<String, Object>> getPaymentModeList(UserRequestDTO userRequestDto, String modeVal) {
        List<Map<String, Object>> paymentModeList = new ArrayList<Map<String, Object>>();
        String strProcName = EssentialCommonDaoConfig.PROCEDURE_GET_PATIENT_PAYMENT_MODE_COMBO;
        try {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery(
                    strProcName);

            /*
             * query.registerStoredProcedureParameter("modeval", String.class,
             * ParameterMode.IN);
             * query.registerStoredProcedureParameter("hosp_code", String.class,
             * ParameterMode.IN);
             * query.registerStoredProcedureParameter("err", String.class,
             * ParameterMode.OUT);
             * query.registerStoredProcedureParameter("resultset", void.class,
             * ParameterMode.REF_CURSOR);
             * query.setParameter("modeval", modeVal);
             * query.setParameter("hosp_code", userRequestDto.getHospitalCode());
             * query.execute();
             * String err = (String) query.getOutputParameterValue("err");
             * if (err != null && !err.isEmpty()) {
             * throw new RuntimeException("Database Error: " + err);
             * }
             */

            query.registerStoredProcedureParameter(1, String.class, ParameterMode.IN); // p_mode
            query.registerStoredProcedureParameter(2, String.class, ParameterMode.IN); // p_hosp_code
            query.registerStoredProcedureParameter(3, String.class, ParameterMode.OUT);// err
            query.registerStoredProcedureParameter(4, void.class, ParameterMode.REF_CURSOR); // resultset

            query.setParameter(1, modeVal); // "1"
            query.setParameter(2, userRequestDto.getHospitalCode());
            query.execute();
            String err = (String) query.getOutputParameterValue(3);
            if (err != null && !err.isEmpty()) {
                log.error("Stored procedure error in Get Payment Mode List | error={}", err);
                throw new RuntimeException("Database Error: " + err);
            }

            List<Object[]> resultList = query.getResultList();
            String[] columns = { "Payment_id", "Payment_Type" };
            paymentModeList = HelperMethods.toListWithColumnNames(resultList, columns);

        } catch (Exception e) {
            log.error("Database error while fetching PaymentModeRepository.getPaymentModeList()", e);
            throw new HISDataAccessException(
                    "PaymentModeRepository.getPaymentModeList() ::executeProcedureByPosition"
                            + ") -> " + e.getMessage());
        }
        return paymentModeList;
    }

}
