package com.statewide.login.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import com.statewide.login.entity.ForgotPassUserVO;
import com.statewide.login.exception.HISDataAccessException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class ForgotPasswordRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public ForgotPassUserVO fetchUserDetailsByMobileNumber(String str_mode, String varUserName) {
        // IMPORTANT: Disable autocommit for refcursor
        entityManager.unwrap(Session.class)
                .doWork(connection -> {
                    try {
                        connection.setAutoCommit(false);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });

        ForgotPassUserVO vo = new ForgotPassUserVO();
        try {
            // For first-time call userId is ALWAYS empty
            Integer varUserId = null;
            StoredProcedureQuery sp = entityManager
                    .createStoredProcedureQuery("pkg_usermgmt.proc_gblt_user_mst_For_mbileOTP_forgotpassword");
            sp.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter(2, Integer.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter(3, String.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter(4, String.class, ParameterMode.OUT); // err
            sp.registerStoredProcedureParameter(5, ResultSet.class, ParameterMode.REF_CURSOR);

            sp.setParameter(1, Integer.parseInt(str_mode));
            sp.setParameter(2, varUserId);
            sp.setParameter(3, varUserName);
            sp.execute();
            String errorMsg = (String) sp.getOutputParameterValue(4);
            if (errorMsg != null && !errorMsg.trim().isEmpty()) {
                throw new RuntimeException("DB Error: " + errorMsg);
            }

            List<Object[]> result = sp.getResultList();
            if (result == null || result.isEmpty()) {
                return null;
            }

            Object[] row = result.get(0);

            // Map to VO

            vo.setVarUserId(row[0] != null ? Long.valueOf(row[0].toString()) : null);
            vo.setVarUserName(row[1] != null ? row[1].toString() : null);
            vo.setVarPassword(row[2] != null ? row[2].toString() : null);
            vo.setVarHospitalCode(row[3] != null ? Long.valueOf(row[3].toString()) : null);
            vo.setVarUserSeatId(row[4] != null ? Long.valueOf(row[4].toString()) : null);
            vo.setVarEmailId(row[5] != null ? row[5].toString() : null);
            // vo.setVarMobileNumber(row[6] != null ? Long.valueOf(row[6].toString()) :
            // null);
            vo.setVarMobileNumber(row[6] != null ? row[6].toString() : null);
        } catch (Exception e) {
            throw new HISDataAccessException(
                    "ForgotPasswordRepository.getUserDetail()::executeProcedureByPosition{call pkg_usermgmt.proc_gblt_user_mst(?,?,?,?,?,?,?,?,?,?)}) -> "
                            + e.getMessage());
        }
        return vo;
    }

    public String checkUserPasswordDetail(String strMode, Long varHospitalCode, Long varUserId,
            Long varUserSeatId, String varNewPassword) {
        String matchCount = "0";
        try {
            // Create native query for function call
            Query query = entityManager.createNativeQuery(
                    "SELECT pkg_usermgmt.proc_gblt_password_record(:p_mode, :p_hospital_code, :p_user_id, :p_seat_id, :p_password) FROM dual");
            query.setParameter("p_mode", strMode);
            query.setParameter("p_hospital_code", varHospitalCode.toString());
            query.setParameter("p_user_id", varUserId.toString());
            query.setParameter("p_seat_id", varUserSeatId.toString());
            query.setParameter("p_password", varNewPassword);

            // Execute and get single result
            matchCount = (String) query.getSingleResult();
        } catch (Exception e) {
            throw new HISDataAccessException(
                    "ForgotPasswordRepository.checkUserPasswordDetail()::executeProcedureByPosition"
                            + "pkg_usermgmt.proc_gblt_password_record"
                            + ") -> " + e.getMessage());
        }
        return matchCount;
    }

    // save password DB resetforgottenuserpassword
    /**
     * Inserting/Update User Master
     * 
     * @param strMode_p
     * @param voUser_p
     * @return
     * @throws Exception
     */
    @Transactional
    public void dmlUserDetail(String strMode_p, ForgotPassUserVO forgotPassUserVO) {

        try {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("pkg_usermgmt.dml_gblt_user_mst");

            // Register input parameters
            query.registerStoredProcedureParameter("p_mode", Integer.class, ParameterMode.IN);
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

            // Set input values
            query.setParameter("p_mode", Integer.parseInt(strMode_p));
            query.setParameter("p_hospital_code", forgotPassUserVO.getVarHospitalCode());
            query.setParameter("p_user_id", forgotPassUserVO.getVarUserId());
            query.setParameter("p_user_name", forgotPassUserVO.getVarUserName());
            query.setParameter("p_seat_id", forgotPassUserVO.getVarUserSeatId());
            query.setParameter("p_emp_no", forgotPassUserVO.getVarEmpNo());
            query.setParameter("p_question_id", forgotPassUserVO.getVarQuestionId());
            query.setParameter("p_hint_answer", forgotPassUserVO.getVarHintAnswer());
            query.setParameter("p_password", forgotPassUserVO.getVarNewPassword());
            query.setParameter("p_old_password", forgotPassUserVO.getVarOldPassword());
            query.setParameter("p_mobile_number", forgotPassUserVO.getVarMobileNumber() == null
                    ? null
                    : forgotPassUserVO.getVarMobileNumber().toString());
            query.setParameter("p_email_id", forgotPassUserVO.getVarEmailId());
            query.setParameter("p_menu_id", forgotPassUserVO.getVarMenuId());

            // Execute
            query.execute();

            // Get output
            String dbError = (String) query.getOutputParameterValue("err");
            if (dbError != null && !dbError.isEmpty()) {
                throw new Exception("Database Error: " + dbError);
            }
            log.info("varnewpassword " + strMode_p + "--" + forgotPassUserVO.getVarNewPassword() + "  -"
                    + forgotPassUserVO.getVarUserSeatId()
                    + "--" + forgotPassUserVO.getVarHospitalCode() +
                    " --" + forgotPassUserVO.getVarUserId() + "== " + forgotPassUserVO.getVarEmpNo() + "=="
                    + forgotPassUserVO.getVarOldPassword() + "--"
                    + forgotPassUserVO.getVarUserName());
            log.info("Inside Dml user Details :::: update password");
        } catch (Exception e) {

            throw new HISDataAccessException("ForgotPasswordRepository.dmlUserDetail()::executeProcedureByPosition"
                    + "pkg_usermgmt.dml_gblt_user_mst"
                    + ") -> " + e.getMessage());
        }

    }

}
