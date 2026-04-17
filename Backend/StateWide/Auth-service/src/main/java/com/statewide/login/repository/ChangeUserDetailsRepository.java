package com.statewide.login.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;
import com.statewide.login.entity.MenuMasterVO;
import com.statewide.login.entity.QuestionVO;
import com.statewide.login.entity.UserMasterVO;
import com.statewide.login.exception.HISDataAccessException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class ChangeUserDetailsRepository {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Fetching Detailed List
     * 
     * @param strMode_p
     * @param voUser_p
     * @return List<Entry>
     * @throws Exception
     * 
     */
    // Fetch Question List For ChangeUserDetails
    @Transactional
    public List<QuestionVO> getQuestionList(String strMode_p, UserMasterVO voUser_p) {

        List<QuestionVO> questionList = new ArrayList<>();
        try {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("pkg_usermgmt.proc_gblt_tables");

            query.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter(2, Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter(3, Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter(4, String.class, ParameterMode.OUT);
            query.registerStoredProcedureParameter(5, void.class, ParameterMode.REF_CURSOR);

            query.setParameter(1, Integer.parseInt(strMode_p));
            query.setParameter(2, voUser_p.getVarHospitalCode());
            query.setParameter(3, voUser_p.getVarUserId());

            query.execute();

            String errMsg = (String) query.getOutputParameterValue(4);
            if (errMsg != null && !errMsg.trim().isEmpty()) {
                throw new RuntimeException("Stored Procedure Error: " + errMsg);
            }

            @SuppressWarnings("unchecked")
            List<Object[]> resultList = query.getResultList();

            for (Object[] row : resultList) {
                QuestionVO vo = new QuestionVO();
                vo.setQuestionId(((Number) row[0]).longValue());
                vo.setQuestionDesc((String) row[1]);
                questionList.add(vo);
            }
        } catch (Exception e) {
            throw new HISDataAccessException(
                    "ChangeUserDetailsRepository.getQuestionList()::executeProcedureByPosition"
                            + "pkg_usermgmt.proc_gblt_tables"
                            + ") -> " + e.getMessage());
        }
        return questionList;
    }

    /**
     * Inserting/Update User Master
     * 
     * @param strMode_p
     * @param voUser_p
     * @return
     * @throws Exception
     */
    @Transactional
    public void dmlUserDetail(String strMode, UserMasterVO voUser_p) {
        try {

            StoredProcedureQuery query = entityManager
                    .createStoredProcedureQuery("pkg_usermgmt.dml_gblt_user_mst");
            // Register input parameters
            query.registerStoredProcedureParameter("p_mode", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_hospital_code", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_user_id", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_user_name", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_seat_id", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_emp_no", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_question_id", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_hint_answer", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_password", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_old_password", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_mobile_number", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_email_id", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_menu_id", String.class, ParameterMode.IN);

            // Register output parameter
            query.registerStoredProcedureParameter("err", String.class, ParameterMode.OUT);

            // Set input values (use empty string if null)
            query.setParameter("p_mode", Integer.parseInt(strMode));
            query.setParameter("p_hospital_code", toIntegerData(voUser_p.getVarHospitalCode()));
            query.setParameter("p_user_id", toIntegerData(voUser_p.getVarUserId()));
            query.setParameter("p_user_name", voUser_p.getVarUserName());
            query.setParameter("p_seat_id", toIntegerData(voUser_p.getVarUserSeatId()));
            query.setParameter("p_emp_no", voUser_p.getVarEmpNo());
            query.setParameter("p_question_id", voUser_p.getVarQuestionId());
            query.setParameter("p_hint_answer", voUser_p.getVarHintAnswer());
            query.setParameter("p_password", voUser_p.getVarPassword());
            query.setParameter("p_old_password", voUser_p.getVarOldPassword());
            query.setParameter("p_mobile_number", voUser_p.getVarMobileNumber());
            query.setParameter("p_email_id", voUser_p.getVarEmailId());
            query.setParameter("p_menu_id", voUser_p.getVarMenuId());

            // Execute procedure
            query.execute();

            // Optional: retrieve output parameter
            String dbError = (String) query.getOutputParameterValue("err");
            if (dbError != null && !dbError.isEmpty()) {
                throw new RuntimeException("Database Error: " + dbError);
            }
            log.info("p_mode = {}", strMode);
            log.info("p_login_user_name = {}", voUser_p.getVarUserName());
            log.info("p_hospital_code = {}", voUser_p.getVarHospitalCode());
            log.info("p_user_id = {}", voUser_p.getVarUserId());
            log.info("p_password = {}", voUser_p.getVarPassword());
            log.info("p_question_id = {}", voUser_p.getVarQuestionId());
            log.info("p_mobile_number = {}", voUser_p.getVarMobileNumber());
            log.info("p_email_id = {}", voUser_p.getVarEmailId());
            log.info("p_menu_id = {}", voUser_p.getVarMenuId());

            log.info("Inside  dmlUserDetail Details ::::");

        } catch (Exception e) {
            throw new RuntimeException(
                    "ChangeUserDetailsRepository.dmlUserDetail -> Error executing procedure: " + e.getMessage(), e);
        }
    }

    private Integer toIntegerData(String value) {
        return (value == null || value.trim().isEmpty()) ? null : Integer.valueOf(value);
    }

    /**
     * Inserting/Update Menu Master
     * 
     * @param strMode_p
     * @param MenuMasterVO menuVO_p
     * @return
     * @throws Exception
     */
    @Transactional
    public void dmlMenuMasterDetail(String strMode_p, MenuMasterVO menuVO_p) {
        try {

            StoredProcedureQuery query = entityManager
                    .createStoredProcedureQuery("pkg_usermgmt.dml_gblt_menu_mst");
            // Register input parameters
            query.registerStoredProcedureParameter("p_mode", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_hospital_code", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_user_id", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_menu_id", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_seat_id", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("gnum_display_order", String.class, ParameterMode.IN);
            // Register output parameter
            query.registerStoredProcedureParameter("err", String.class, ParameterMode.OUT);
            // Set input values (use empty string if null)
            query.setParameter("p_mode", Integer.parseInt(strMode_p));
            query.setParameter("p_hospital_code", menuVO_p.getVarHospitalCode());
            query.setParameter("p_user_id", menuVO_p.getVarUserId());
            query.setParameter("p_menu_id", menuVO_p.getVarMenuId());
            query.setParameter("p_seat_id", menuVO_p.getVarSeatId());
            query.setParameter("gnum_display_order", menuVO_p.getVarDisplayOrder());
            // Execute procedure
            query.execute();
            // Retrieve output parameter safely
            // Check if there was an error
            String dbError = (String) query.getOutputParameterValue("err");
            if (dbError != null && !dbError.isEmpty()) {
                throw new RuntimeException("Database Error: " + dbError);
            }

            log.info("p_mode = {}", strMode_p);
            log.info("p_hospital_code = {}", menuVO_p.getVarHospitalCode());
            log.info("p_user_id = {}", menuVO_p.getVarUserId());
            log.info("p_menu_id = {}", menuVO_p.getVarMenuId());
            log.info("p_seat_id = {}", menuVO_p.getVarSeatId());
            log.info("p_display_order = {}", menuVO_p.getVarDisplayOrder());
            log.info("Inside  dmlMenuMasterDetail  ::::");

        } catch (Exception e) {
            throw new HISDataAccessException(
                    "ChangeUserDetailsRepository.dmlMenuMasterDetail()::hisDAO_p.executeProcedureByPosition"
                            + ") -> " + e.getMessage());
        }

    }

    /**
     * Fetching User Menu Detail
     * 
     * @param strMode_p
     * @param UserMasterVO voUser
     * @return List<MenuMasterVO>
     * @throws Exception
     */
    @Transactional
    public List<MenuMasterVO> fetchUserMenuDetail(String strMode_p, UserMasterVO voUser) {
        List<MenuMasterVO> menuMasterList = new ArrayList<>(); // declare outside try
        // log.info("Mode: " + strMode_p + ", Hospital: " + voUser.getVarHospitalCode()
        // +
        // ", User: " + voUser.getVarUserId() + ", Seat: " + voUser.getVarUserSeatId());
        try {
            StoredProcedureQuery storedProcedure = entityManager
                    .createStoredProcedureQuery("pkg_usermgmt.proc_gblt_menu_mst");

            storedProcedure.registerStoredProcedureParameter("p_mode", Integer.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("p_hospital_code", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("p_user_id", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("p_seat_id", String.class, ParameterMode.IN);

            storedProcedure.registerStoredProcedureParameter("err", String.class, ParameterMode.OUT);
            storedProcedure.registerStoredProcedureParameter("resultset", void.class, ParameterMode.REF_CURSOR);

            // Set input parameters
            storedProcedure.setParameter("p_mode", Integer.parseInt(strMode_p));
            storedProcedure.setParameter("p_hospital_code",
                    voUser.getVarHospitalCode() != null ? voUser.getVarHospitalCode() : null);
            storedProcedure.setParameter("p_user_id", voUser.getVarUserId() != null ? voUser.getVarUserId() : null);
            storedProcedure.setParameter("p_seat_id",
                    voUser.getVarUserSeatId() != null ? voUser.getVarUserSeatId() : null);

            storedProcedure.execute();

            // Get error message from OUT parameter
            String dbError = (String) storedProcedure.getOutputParameterValue("err");
            if (dbError != null && !dbError.isEmpty()) {
                throw new RuntimeException("Database Error: " + dbError);
            }

            // Get result list
            List<Object[]> results = storedProcedure.getResultList();

            // Map Object[] to MenuMasterVO
            for (Object[] row : results) {
                MenuMasterVO menuMasterVO = new MenuMasterVO();
                if ("1".equals(strMode_p)) {
                    // Mode 1: Get menu name and URL
                    menuMasterVO.setVarMenuName(row[0] != null ? row[0].toString() : null);
                    menuMasterVO.setVarURL(row[1] != null ? row[1].toString() : null);
                    menuMasterVO.setVarMenuId(row[2] != null ? row[2].toString() : null);
                } else if ("4".equals(strMode_p)) {
                    menuMasterVO.setVarMenuId(row[0] != null ? row[0].toString() : null);
                    menuMasterVO.setVarURL(row[1] != null ? row[1].toString() : null);
                } else {
                    menuMasterVO.setVarMenuId(row[0] != null ? row[0].toString() : null);
                    menuMasterVO.setVarMenuName(row[1] != null ? row[1].toString() : null);
                    menuMasterVO.setVarURL(row[2] != null ? row[2].toString() : null);
                }

                menuMasterList.add(menuMasterVO);
            }

        } catch (Exception e) {
            throw new HISDataAccessException(
                    "ChangeUserDetailsRepository.fetchUserMenuDetail()::executeProcedureByPosition"
                            + ") -> " + e.getMessage());
        }

        return menuMasterList;
    }

}
