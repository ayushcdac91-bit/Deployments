package com.statewide.login.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import com.statewide.login.entity.UserLoginLogVO;
import com.statewide.login.entity.UserMasterVO;
import com.statewide.login.requestdto.LoginUserRequestDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class LoginRepository {
        @PersistenceContext
        private EntityManager entityManager;

        /**
         * Fetching User Detail
         * 
         * @param strMode_p
         * @param request
         * @return List<LoginVO>
         * @throws Exception
         */
        @Transactional
        public List<UserMasterVO> getUserDetail(String strMode_p, LoginUserRequestDTO request) {
                List<UserMasterVO> userMasterData = new ArrayList<>();
                try {
                        StoredProcedureQuery query = entityManager
                                        .createStoredProcedureQuery("pkg_usermgmt.proc_gblt_user_mst");

                        // REGISTER ONLY POSITIONAL PARAMETERS --------------------------
                        query.registerStoredProcedureParameter("p_mode", Integer.class, ParameterMode.IN);
                        query.registerStoredProcedureParameter("p_hospital_code", Integer.class, ParameterMode.IN);
                        query.registerStoredProcedureParameter("p_user_id", Integer.class, ParameterMode.IN);
                        query.registerStoredProcedureParameter("p_user_name", String.class, ParameterMode.IN);
                        query.registerStoredProcedureParameter("p_seat_id", Integer.class, ParameterMode.IN);
                        query.registerStoredProcedureParameter("p_emp_no", String.class, ParameterMode.IN);
                        query.registerStoredProcedureParameter("p_question_id", String.class, ParameterMode.IN);
                        query.registerStoredProcedureParameter("p_hint_answer", String.class, ParameterMode.IN);
                        // Output parameters
                        query.registerStoredProcedureParameter("err", String.class, ParameterMode.OUT); // err
                        query.registerStoredProcedureParameter("resultset", void.class, ParameterMode.REF_CURSOR); // resultset

                        // SET PARAMETERS ------------------------------------------------
                        query.setParameter("p_mode", Optional.ofNullable(toIntegerData(strMode_p)).orElse(null));
                        query.setParameter("p_hospital_code",
                                        Optional.ofNullable(toIntegerData(request.getVarHospitalCode())).orElse(null));
                        query.setParameter("p_user_id",
                                        Optional.ofNullable(toIntegerData(request.getVarUserId())).orElse(null));
                        query.setParameter("p_user_name", Optional.ofNullable(request.getVarUserName()).orElse(null));
                        query.setParameter("p_seat_id",
                                        Optional.ofNullable(toIntegerData(request.getVarSeatId())).orElse(null));
                        query.setParameter("p_emp_no", Optional.ofNullable(request.getVarEmpNo()).orElse(null));
                        query.setParameter("p_question_id",
                                        Optional.ofNullable(request.getVarQuestionId()).orElse(null));
                        query.setParameter("p_hint_answer",
                                        Optional.ofNullable(request.getVarHintAnswer()).orElse(null));

                        // EXECUTE --------------------------------------------------------
                        query.execute();
                        String dbError = (String) query.getOutputParameterValue("err");
                        if (dbError != null && !dbError.isEmpty()) {
                                throw new RuntimeException("Database Error: " + dbError);
                        }

                        @SuppressWarnings("unchecked")
                        List<Object[]> results = query.getResultList();

                        for (Object[] row : results) {

                                UserMasterVO userMasterVO = new UserMasterVO();

                                userMasterVO.setVarUserId(row[0] != null ? row[0].toString() : null);
                                userMasterVO.setVarUserName(row[1] != null ? row[1].toString() : null);
                                userMasterVO.setVarPassword(row[2] != null ? row[2].toString() : null);
                                userMasterVO.setVarUserSeatId(row[3] != null ? row[3].toString() : null);
                                userMasterVO.setVarEmpNo(row[4] != null ? row[4].toString() : null);
                                userMasterVO.setVarHospitalCode(row[5] != null ? row[5].toString() : null);
                                userMasterVO.setVarUserLevel(row[6] != null ? row[6].toString() : null);
                                userMasterVO.setVarUsrName(row[7] != null ? row[7].toString() : null);
                                userMasterVO.setVarDesignation(row[8] != null ? row[8].toString() : null);
                                userMasterVO.setVarMobileNumber(row[9] != null ? row[9].toString() : null);
                                userMasterVO.setVarEmailId(row[10] != null ? row[10].toString() : null);
                                userMasterVO.setVarDistrictId(row[11] != null ? row[11].toString() : null);
                                userMasterVO.setVarDistrictName(row[12] != null ? row[12].toString() : null);
                                userMasterVO.setVarQuestionId(row[13] != null ? row[13].toString() : null);
                                userMasterVO.setVarHintAnswer(row[14] != null ? row[14].toString() : null);
                                userMasterVO.setVarLock(row[15] != null ? row[15].toString() : null);
                                userMasterVO.setVarChangePasswordDate(row[16] != null ? row[16].toString() : null);
                                userMasterVO.setVarMenuId(row[17] != null ? row[17].toString() : null);
                                userMasterVO.setVarDefaultMenuURL(row[18] != null ? row[18].toString() : null);
                                userMasterVO.setVarDefaultMenuModule(row[19] != null ? row[19].toString() : null);
                                userMasterVO.setVarDefaultMenuName(row[20] != null ? row[20].toString() : null);
                                userMasterVO.setVarIsAutoRefresh(
                                                row[21] != null ? Long.valueOf(row[21].toString()) : null);

                                userMasterData.add(userMasterVO);
                        }

                } catch (Exception e) {
                        throw new RuntimeException(
                                        "LoginRepository.getUserDetail()::executeProcedureByPosition" + ") -> "
                                                        + e.getMessage());
                }
                return userMasterData;
        }

        private Integer toIntegerData(String value) {
                return (value == null || value.trim().isEmpty()) ? null : Integer.valueOf(value);
        }

        /**
         * Inserting/Update User Unsuccessful Log
         * 
         * @param strMode
         * @param voUserLoginLog
         * @return void
         * @throws Exception
         */

        @Transactional
        public void dmlUserLoginLog(String strMode, UserLoginLogVO voUserLoginLog) {
                log.info("Inside Dml UserLoginLog Details :::: update UserLoginLog");
                try {
                        StoredProcedureQuery query = entityManager
                                        .createStoredProcedureQuery("pkg_usermgmt.dml_user_login_log_withmac");

                        // Register input parameters
                        query.registerStoredProcedureParameter("p_mode", String.class, ParameterMode.IN);
                        query.registerStoredProcedureParameter("p_hospital_code", Integer.class, ParameterMode.IN);
                        query.registerStoredProcedureParameter("p_user_id", Integer.class, ParameterMode.IN);
                        query.registerStoredProcedureParameter("p_login_user_name", String.class, ParameterMode.IN);
                        query.registerStoredProcedureParameter("p_ip_address", String.class, ParameterMode.IN);
                        query.registerStoredProcedureParameter("p_seat_id", Integer.class, ParameterMode.IN);
                        query.registerStoredProcedureParameter("p_mac_address", String.class, ParameterMode.IN);
                        query.registerStoredProcedureParameter("p_client_sso_ticket", String.class, ParameterMode.IN);
                        query.registerStoredProcedureParameter("p_app_host", String.class, ParameterMode.IN);

                        // Register output parameter
                        query.registerStoredProcedureParameter("err", String.class, ParameterMode.OUT);

                        // Set input values (use empty string if null)
                        query.setParameter("p_mode", strMode);
                        query.setParameter("p_hospital_code",
                                        Optional.ofNullable(toIntegerData(voUserLoginLog.getVarHospitalCode()))
                                                        .orElse(null));
                        query.setParameter("p_user_id",
                                        Optional.ofNullable(toIntegerData(voUserLoginLog.getVarUserId())).orElse(null));
                        query.setParameter("p_login_user_name",
                                        Optional.ofNullable(voUserLoginLog.getVarUserName()).orElse(null));
                        query.setParameter("p_ip_address",
                                        Optional.ofNullable(voUserLoginLog.getVarIPAddress()).orElse(null));
                        query.setParameter("p_seat_id",
                                        Optional.ofNullable(toIntegerData(voUserLoginLog.getVarSeatId())).orElse(null));
                        query.setParameter("p_mac_address",
                                        Optional.ofNullable(voUserLoginLog.getVarMacAddress()).orElse(null));
                        query.setParameter("p_client_sso_ticket",
                                        Optional.ofNullable(voUserLoginLog.getVarClientSSOTicketId()).orElse(null));
                        query.setParameter("p_app_host",
                                        Optional.ofNullable(voUserLoginLog.getVarAppHost()).orElse(null));

                        // Execute procedure
                        query.execute();

                        // Optional: retrieve output parameter
                        String dbError = (String) query.getOutputParameterValue("err");
                        if (dbError != null && !dbError.isEmpty()) {
                                throw new RuntimeException("Database Error: " + dbError);
                        }

                        log.info("p_mode = {} p_login_user_name = {} p_hospital_code = {} p_user_id = {} p_ip_address = {} p_seat_id = {} p_mac_address = {} p_client_sso_ticket = {} p_app_host = {}",
                                        strMode, voUserLoginLog.getVarUserName(), voUserLoginLog.getVarHospitalCode(),
                                        voUserLoginLog.getVarUserId(), voUserLoginLog.getVarIPAddress(),
                                        voUserLoginLog.getVarSeatId(),
                                        voUserLoginLog.getVarMacAddress(), voUserLoginLog.getVarClientSSOTicketId(),
                                        voUserLoginLog.getVarAppHost());

                } catch (Exception e) {
                        throw new RuntimeException(
                                        "LoginRepository.dmlUserLoginLog() -> Error executing procedure: "
                                                        + e.getMessage(),
                                        e);
                }
        }

        /**
         * Fetching User Detail
         * 
         * @param strMode_p
         * @param voUserLoginLog_p
         * 
         * @return List<UserLoginLogVO>
         * @throws Exception
         */

        @Transactional
        public List<UserLoginLogVO> getUserLoginLog(String strMode, UserLoginLogVO voUserLoginLog, String frDate,
                        String toDate) {
                List<UserLoginLogVO> loginLog = new ArrayList<>();
                try {
                        StoredProcedureQuery query = entityManager
                                        .createStoredProcedureQuery("pkg_usermgmt.proc_user_login_log");

                        // Input parameters
                        query.registerStoredProcedureParameter("p_mode", String.class, ParameterMode.IN);
                        query.registerStoredProcedureParameter("p_hospital_code", Integer.class, ParameterMode.IN);
                        query.registerStoredProcedureParameter("p_user_id", Integer.class, ParameterMode.IN);
                        query.registerStoredProcedureParameter("p_login_user_name", String.class, ParameterMode.IN);
                        query.registerStoredProcedureParameter("p_ip_address", String.class, ParameterMode.IN);
                        query.registerStoredProcedureParameter("p_seat_id", Integer.class, ParameterMode.IN);
                        query.registerStoredProcedureParameter("p_from_date", String.class, ParameterMode.IN);
                        query.registerStoredProcedureParameter("p_to_date", String.class, ParameterMode.IN);

                        // Output parameters
                        query.registerStoredProcedureParameter("err", String.class, ParameterMode.OUT);
                        query.registerStoredProcedureParameter("resultset", void.class, ParameterMode.REF_CURSOR);

                        // Set input values
                        query.setParameter("p_mode", strMode);
                        query.setParameter("p_hospital_code",
                                        Optional.ofNullable(toIntegerData(voUserLoginLog.getVarHospitalCode()))
                                                        .orElse(null));
                        query.setParameter("p_user_id",
                                        Optional.ofNullable(toIntegerData(voUserLoginLog.getVarUserId())).orElse(null));
                        query.setParameter("p_login_user_name",
                                        Optional.ofNullable(voUserLoginLog.getVarUserName()).orElse(null));
                        query.setParameter("p_ip_address",
                                        Optional.ofNullable(voUserLoginLog.getVarIPAddress()).orElse(null));
                        query.setParameter("p_seat_id",
                                        Optional.ofNullable(toIntegerData(voUserLoginLog.getVarSeatId())).orElse(null));
                        query.setParameter("p_from_date", frDate != null ? frDate : "");
                        query.setParameter("p_to_date", toDate != null ? toDate : "");

                        // Execute
                        query.execute();

                        // Check error
                        String dbError = (String) query.getOutputParameterValue("err");
                        if (dbError != null && !dbError.isEmpty()) {
                                throw new RuntimeException("Database Error: " + dbError);
                        }

                        @SuppressWarnings("unchecked")
                        List<?> resultList = query.getResultList();
                        log.info("Got total results: " + resultList);

                        if (resultList != null) {
                                for (Object rowObj : resultList) {
                                        UserLoginLogVO vo = new UserLoginLogVO();

                                        // If the query returns only varUnsuccessfulCount
                                        if (rowObj != null) {
                                                vo.setVarUnsuccessfulCount(rowObj.toString());
                                        }

                                        loginLog.add(vo);
                                }
                        }

                } catch (Exception e) {
                        throw new RuntimeException(
                                        "LoginRepository.getUserLoginLog()::hisDAO_p.executeProcedureByPosition"
                                                        + ") -> "
                                                        + e.getMessage());
                }
                return loginLog;
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
        public void dmlUserDetail(String strMode, UserMasterVO userMasterVO) {
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
                        query.setParameter("p_hospital_code",
                                        Optional.ofNullable(toIntegerData(userMasterVO.getVarHospitalCode()))
                                                        .orElse(null));
                        query.setParameter("p_user_id",
                                        Optional.ofNullable(toIntegerData(userMasterVO.getVarUserId())).orElse(null));
                        query.setParameter("p_user_name",
                                        Optional.ofNullable(userMasterVO.getVarUserName()).orElse(null));
                        query.setParameter("p_seat_id",
                                        Optional.ofNullable(toIntegerData(userMasterVO.getVarUserSeatId()))
                                                        .orElse(null));
                        query.setParameter("p_emp_no", Optional.ofNullable(userMasterVO.getVarEmpNo()).orElse(null));
                        query.setParameter("p_question_id",
                                        Optional.ofNullable(userMasterVO.getVarQuestionId()).orElse(null));
                        query.setParameter("p_hint_answer",
                                        Optional.ofNullable(userMasterVO.getVarHintAnswer()).orElse(null));
                        query.setParameter("p_password",
                                        Optional.ofNullable(userMasterVO.getVarPassword()).orElse(null));
                        query.setParameter("p_old_password",
                                        Optional.ofNullable(userMasterVO.getVarOldPassword()).orElse(null));
                        query.setParameter("p_mobile_number",
                                        Optional.ofNullable(userMasterVO.getVarMobileNumber()).orElse(null));
                        query.setParameter("p_email_id",
                                        Optional.ofNullable(userMasterVO.getVarEmailId()).orElse(null));
                        query.setParameter("p_menu_id", Optional.ofNullable(userMasterVO.getVarMenuId()).orElse(null));

                        // Execute procedure
                        query.execute();

                        // Optional: retrieve output parameter
                        String dbError = (String) query.getOutputParameterValue("err");
                        if (dbError != null && !dbError.isEmpty()) {
                                throw new RuntimeException("Database Error: " + dbError);
                        }

                        log.info("Inside  dmlUserDetail Details :::: Lock Account dmlUserDetail");

                } catch (Exception e) {
                        throw new RuntimeException(
                                        "LoginRepository.dmlUserDetail -> Error executing procedure: " + e.getMessage(),
                                        e);
                }
        }

}
