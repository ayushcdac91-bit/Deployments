package com.statewide.login.repository;

import java.sql.SQLException;
import java.util.List;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import com.statewide.login.entity.ChangePasswordVO;
import com.statewide.login.entity.UserMasterVO;
import com.statewide.login.exception.HISDataAccessException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class ChangePasswordRepository {
    @PersistenceContext
    private EntityManager entityManager;

    // Fetch user daetails for change password
    public UserMasterVO fetchUserDetails(String str_mode, String varUserName) {
        entityManager.unwrap(Session.class)
                .doWork(connection -> {
                    try {
                        connection.setAutoCommit(false);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
        UserMasterVO vo = new UserMasterVO();
        try {
            StoredProcedureQuery sp = entityManager
                    .createStoredProcedureQuery("pkg_usermgmt.proc_gblt_user_mst");

            // REGISTER ONLY POSITIONAL PARAMETERS --------------------------
            sp.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter(2, Integer.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter(3, Integer.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter(4, String.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter(5, Integer.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter(6, String.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter(7, String.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter(8, String.class, ParameterMode.IN);

            sp.registerStoredProcedureParameter(9, String.class, ParameterMode.OUT); // err
            sp.registerStoredProcedureParameter(10, void.class, ParameterMode.REF_CURSOR); // resultset

            // SET PARAMETERS ------------------------------------------------
            sp.setParameter(1, Integer.parseInt(str_mode));
            sp.setParameter(2, null);
            sp.setParameter(3, null);
            sp.setParameter(4, varUserName);
            sp.setParameter(5, null);
            sp.setParameter(6, null);
            sp.setParameter(7, null);
            sp.setParameter(8, null);

            // EXECUTE --------------------------------------------------------
            sp.execute();

            // READ OUT PARAM
            // String err = (String) sp.getOutputParameterValue(9);
            // System.out.println("ERR = " + err);

            // Check error
            String dbError = (String) sp.getOutputParameterValue(9);
            if (dbError != null && !dbError.isEmpty()) {
                throw new RuntimeException("Database Error: " + dbError);
            }

            List<Object[]> results = sp.getResultList();
            Object[] row = results.get(0);

            // setparameters for entity
            vo.setVarUserId(row[0] != null ? row[0].toString() : null);
            vo.setVarUserName(str(row[1]));
            vo.setVarPassword(str(row[2]));
            vo.setVarUserSeatId(row[3] != null ? row[3].toString() : null);
            vo.setVarEmpNo(str(row[4]));
            vo.setVarHospitalCode(row[5] != null ? row[5].toString() : null);
            vo.setVarUserLevel(row[6] != null ? row[6].toString() : null);
            vo.setVarUsrName(str(row[7]));
            vo.setVarDesignation(str(row[8]));
            vo.setVarMobileNumber(row[9] != null ? row[9].toString() : null);
            vo.setVarEmailId(str(row[10]));
            vo.setVarDistrictId(row[11] != null ? row[11].toString() : null);
            vo.setVarDistrictName(str(row[12]));
            vo.setVarQuestionId(row[13] != null ? row[13].toString() : null);
            vo.setVarHintAnswer(str(row[14]));
            vo.setVarLock(row[15] != null ? row[15].toString() : null);
            vo.setVarChangePasswordDate(str(row[16]));
            vo.setVarMenuId(row[17] != null ? row[17].toString() : null);
            vo.setVarDefaultMenuURL(str(row[18]));
            vo.setVarDefaultMenuModule(str(row[19]));
            vo.setVarDefaultMenuName(str(row[20]));
            vo.setVarIsAutoRefresh(row[21] != null ? Long.valueOf(row[21].toString()) : null);
        } catch (Exception e) {
            throw new HISDataAccessException(
                    "ChangePasswordRepository.fetchUserDetails() for change password::executeProcedureByPosition"
                            + ") -> " + e.getMessage());
        }
        return vo;

    }

    /**
     * Inserting/Update User Master
     * 
     * @param strMode_p
     * @param changePassVO
     * @return
     */
    // change userpassword in db
    @Transactional
    public void changeUserPasswordDetail(String str_mode, ChangePasswordVO changePassVO) {

        try {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("pkg_usermgmt.dml_gblt_user_mst");

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

            query.registerStoredProcedureParameter("err", String.class, ParameterMode.OUT);

            query.setParameter("p_mode", Integer.parseInt(str_mode));
            query.setParameter("p_hospital_code", changePassVO.getVarHospitalCode());
            query.setParameter("p_user_id", changePassVO.getVarUserId());
            query.setParameter("p_user_name", changePassVO.getVarUserName());
            query.setParameter("p_seat_id", changePassVO.getVarUserSeatId());
            query.setParameter("p_emp_no", null);
            query.setParameter("p_question_id", null);
            query.setParameter("p_hint_answer", null);
            query.setParameter("p_password", changePassVO.getVarNewPassword());
            query.setParameter("p_old_password", changePassVO.getVarOldPassword());
            query.setParameter("p_mobile_number", null);
            query.setParameter("p_email_id", null);
            query.setParameter("p_menu_id", null);

            query.execute();

            String dbError = (String) query.getOutputParameterValue("err");
            if (dbError != null && !dbError.isEmpty()) {
                throw new Exception("Database Error: " + dbError);
            }
            log.info("varnewpassword " + str_mode + "--" + changePassVO.getVarNewPassword() + "  -"
                    + changePassVO.getVarUserSeatId()
                    + "--" + changePassVO.getVarHospitalCode() +
                    " --" + changePassVO.getVarUserId() + "==   ==" + changePassVO.getVarOldPassword() + "--"
                    + changePassVO.getVarUserName());
            log.info("Inside Dml user Details :::: update password");
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error executing procedure PROC_DML_USER_MASTER: changeUserPasswordDetail()::executeProcedureByPosition"
                            + e.getMessage(),
                    e);

        }

    }

    private String str(Object o) {
        return o == null ? null : o.toString();
    }

    public String checkUserPasswordDetail(String strMode, ChangePasswordVO changePassVO) {
        // Create native query for function call
        String matchCount = "0";
        try {
            Query query = entityManager.createNativeQuery(
                    "SELECT pkg_usermgmt.proc_gblt_password_record(:p_mode, :p_hospital_code, :p_user_id, :p_seat_id, :p_password) FROM dual");
            query.setParameter("p_mode", strMode);
            query.setParameter("p_hospital_code", changePassVO.getVarHospitalCode().toString());
            query.setParameter("p_user_id", changePassVO.getVarUserId().toString());
            query.setParameter("p_seat_id", changePassVO.getVarUserSeatId().toString());
            query.setParameter("p_password", changePassVO.getVarNewPassword());

            // Execute and get single result
            matchCount = (String) query.getSingleResult();
        } catch (Exception e) {
            throw new RuntimeException(
                    "ChangePasswordRepository.checkUserPasswordDetail()::executeProcedureByPosition"
                            + "pkg_usermgmt.proc_gblt_password_record"
                            + ") -> " + e.getMessage());
        }
        return matchCount;
    }
}
