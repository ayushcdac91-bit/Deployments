package com.statewide.login.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import com.statewide.login.entity.UserLoginLogVO;
import com.statewide.login.exception.HISDataAccessException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class UserLogDetailRepository {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Fetching User Detail
     * 
     * @param strMode_p
     * @param voUserLoginLog_p
     * @return List<UserLoginLogVO>
     * @throws Exception
     */
    @Transactional
    public List<UserLoginLogVO> getUserLoginLog(String strMode_p, UserLoginLogVO voUserLoginLog_p, String frDate,
            String toDate) {
        List<UserLoginLogVO> loginLog = new ArrayList<>();
        try {
            StoredProcedureQuery query = entityManager
                    .createStoredProcedureQuery("pkg_usermgmt.proc_user_login_log");

            query.registerStoredProcedureParameter("p_mode", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_hospital_code", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_user_id", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_login_user_name", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_ip_address", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_seat_id", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_from_date", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_to_date", String.class, ParameterMode.IN);

            query.registerStoredProcedureParameter("err", String.class, ParameterMode.OUT);
            query.registerStoredProcedureParameter("resultset", void.class, ParameterMode.REF_CURSOR);

            query.setParameter("p_mode", Optional.ofNullable(strMode_p).orElse(null));
            query.setParameter("p_hospital_code",
                    Optional.ofNullable(toIntegerData(voUserLoginLog_p.getVarHospitalCode())).orElse(null));
            query.setParameter("p_user_id",
                    Optional.ofNullable(toIntegerData(voUserLoginLog_p.getVarUserId())).orElse(null));
            query.setParameter("p_login_user_name",
                    Optional.ofNullable(voUserLoginLog_p.getVarUserName()).orElse(null));
            query.setParameter("p_ip_address", Optional.ofNullable(voUserLoginLog_p.getVarIPAddress()).orElse(null));
            query.setParameter("p_seat_id",
                    Optional.ofNullable(toIntegerData(voUserLoginLog_p.getVarUserSeatId())).orElse(null));
            query.setParameter("p_from_date", frDate != null ? frDate : "");
            query.setParameter("p_to_date", toDate != null ? toDate : "");

            query.execute();

            String dbError = (String) query.getOutputParameterValue("err");
            if (dbError != null && !dbError.isEmpty()) {
                throw new RuntimeException("Database Error: " + dbError);
            }

            @SuppressWarnings("unchecked")
            List<Object[]> results = query.getResultList();

            for (Object[] row : results) {
                UserLoginLogVO vo = new UserLoginLogVO();

                vo.setVarLoginStatus(row[0] != null ? row[0].toString() : null);
                vo.setVarUserLoginDate(row[1] != null ? row[1].toString() : null);
                vo.setVarUserLoginTime(row[2] != null ? row[2].toString() : null);
                vo.setVarUserLogoutDate(row[3] != null ? row[3].toString() : null);
                vo.setVarUserLogoutTime(row[4] != null ? row[4].toString() : null);
                vo.setVarIPAddress(row[5] != null ? row[5].toString() : null);
                vo.setVarCounterNumber(row[6] != null ? row[6].toString() : null);
                vo.setDt(row[7] != null ? ((java.sql.Timestamp) row[7]).toLocalDateTime() : null);

                loginLog.add(vo);
            }

        } catch (Exception e) {
            throw new HISDataAccessException(
                    "UserLogDetailRepository.getUserLoginLog()::executeProcedureByPosition" + ") -> " + e.getMessage());
        }
        return loginLog;

    }

    private Integer toIntegerData(String value) {
        return (value == null || value.trim().isEmpty()) ? null : Integer.valueOf(value);
    }

}
