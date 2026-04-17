package com.cdac.statewide.hiscommon.service.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.cdac.statewide.hiscommon.service.entity.MenuMasterVO;
import com.cdac.statewide.hiscommon.service.exception.HISDataAccessException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class MenuDataRepository {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Fetching User Menu Detail
     * 
     * @param strMode_p
     * @param MenuMasterVO menuMasterVO
     * @return List<MenuMasterVO>
     * @throws Exception
     */
    @Transactional
    public List<MenuMasterVO> getUserMenuDetail(String strMode_p, MenuMasterVO menuMasterVO) {
        List<MenuMasterVO> menuMasterList = new ArrayList<>();
        // log.info("Hospital Code {} UserId {} SeatId {}" +
        // menuMasterVO.getVarHospitalCode()
        // + menuMasterVO.getVarUserId() + menuMasterVO.getVarUserSeatId());
        try {
            StoredProcedureQuery storedProcedure = entityManager
                    .createStoredProcedureQuery("pkg_usermgmt.proc_gblt_menu_mst");

            storedProcedure.registerStoredProcedureParameter("p_mode", Integer.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("p_hospital_code", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("p_user_id", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("p_seat_id", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("err", String.class, ParameterMode.OUT);
            storedProcedure.registerStoredProcedureParameter("resultset", void.class, ParameterMode.REF_CURSOR);

            storedProcedure.setParameter("p_mode", Optional.ofNullable(Integer.parseInt(strMode_p)).orElse(null));
            storedProcedure.setParameter("p_hospital_code",
                    Optional.ofNullable(menuMasterVO.getVarHospitalCode()).orElse(null));
            storedProcedure.setParameter("p_user_id", Optional.ofNullable(menuMasterVO.getVarUserId()).orElse(null));
            storedProcedure.setParameter("p_seat_id",
                    Optional.ofNullable(menuMasterVO.getVarUserSeatId()).orElse(null));

            storedProcedure.execute();

            String dbError = (String) storedProcedure.getOutputParameterValue("err");
            if (dbError != null && !dbError.isEmpty()) {
                log.error("Database Error : {} " + dbError);
                throw new RuntimeException("Database Error: " + dbError);
            }

            List<Object[]> results = storedProcedure.getResultList();

            // Map Object[] to MenuMasterVO
            for (Object[] row : results) {
                MenuMasterVO menuMasterDataVO = new MenuMasterVO();
                if ("1".equals(strMode_p)) {
                    // Mode 1: Get menu name and URL with mode 1
                    menuMasterDataVO.setVarMenuName(row[0] != null ? row[0].toString() : null);
                    menuMasterDataVO.setVarURL(row[1] != null ? row[1].toString() : null);
                    menuMasterDataVO.setVarMenuId(row[2] != null ? row[2].toString() : null);

                } else if ("4".equals(strMode_p)) {
                    // Mode 4: Get menu ID and URL with mode 4

                    menuMasterDataVO.setVarMenuId(row[0] != null ? row[0].toString() : null);
                    menuMasterDataVO.setVarURL(row[1] != null ? row[1].toString() : null);

                } else {
                    // Default mapping if needed

                    menuMasterDataVO.setVarMenuId(row[0] != null ? row[0].toString() : null);
                    menuMasterDataVO.setVarMenuName(row[1] != null ? row[1].toString() : null);
                    menuMasterDataVO.setVarURL(row[2] != null ? row[2].toString() : null);

                }

                menuMasterList.add(menuMasterDataVO);
            }

        } catch (Exception e) {
            log.error("Database error while fetching Get Menu Data {}", e);
            throw new HISDataAccessException(
                    "MenuDataRepository.getUserMenuDetail()::executeProcedureByPosition"
                            + ") -> " + e.getMessage());
        }

        return menuMasterList;
    }

    /**
     * Fetching get SystemDate
     * 
     * @param strMode_p
     *
     * @return List<MenuMasterVO>
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @Transactional
    public List<MenuMasterVO> getSystemDate(String str_mode) {
        List<MenuMasterVO> systemdateList = new ArrayList<>();
        try {

            StoredProcedureQuery query = entityManager
                    .createStoredProcedureQuery("pkg_usermgmt.proc_gblt_user_mst");

            query.registerStoredProcedureParameter("p_mode", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_hospital_code", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_user_id", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_user_name", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_seat_id", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_emp_no", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_question_id", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_hint_answer", String.class, ParameterMode.IN);

            query.registerStoredProcedureParameter("err", String.class, ParameterMode.OUT);
            query.registerStoredProcedureParameter("resultset", void.class, ParameterMode.REF_CURSOR);

            // Set values
            query.setParameter("p_mode", Integer.parseInt(str_mode)); // Required
            query.setParameter("p_hospital_code", null);
            query.setParameter("p_user_id", null);
            query.setParameter("p_user_name", null);
            query.setParameter("p_seat_id", null);
            query.setParameter("p_emp_no", null);
            query.setParameter("p_question_id", null);
            query.setParameter("p_hint_answer", null);

            query.execute();

            String dbError = (String) query.getOutputParameterValue("err");
            if (dbError != null && !dbError.isEmpty()) {
                throw new RuntimeException("Database Error: " + dbError);
            }

            List<Object[]> results = query.getResultList();
            for (Object[] row : results) {

                MenuMasterVO menuDateTimeVO = new MenuMasterVO();
                menuDateTimeVO.setVarCurrentDate((String) row[0]);
                menuDateTimeVO.setVarCurrentMonth((String) row[1]);
                menuDateTimeVO.setVarCurrentYear((String) row[2]);
                menuDateTimeVO.setVarCurrentHour((String) row[3]);
                menuDateTimeVO.setVarCurrentMinute((String) row[4]);
                menuDateTimeVO.setVarCurrentSecond((String) row[5]);
                systemdateList.add(menuDateTimeVO);
            }
        } catch (Exception e) {
            throw new HISDataAccessException(
                    "MenuDataRepository.getSystemDate()::executeProcedureByPosition"
                            + "pkg_usermgmt.proc_gblt_user_mst"
                            + ") -> " + e.getMessage());
        }
        return systemdateList;

    }

    /**
     * To Fetch checkBackDateDayEnd Flag Details
     * 
     * @param MenuMasterVO menuMasterVO
     * @return String
     * @throws Exception
     */
    @Transactional
    public String checkBackDateDayEnd(String mode, MenuMasterVO menuMasterVO) {
        Object result = null;
        try {
            String sql = "SELECT pkg_reg_util.fun_check_backdate_dayend(:mode, :userId, :hospCode)";

            Query query = entityManager.createNativeQuery(sql);
            query.setParameter("mode", mode);
            query.setParameter("userId",
                    menuMasterVO.getVarUserId() != null ? menuMasterVO.getVarUserId().toString() : null);

            query.setParameter("hospCode",
                    menuMasterVO.getVarHospitalCode() != null ? menuMasterVO.getVarHospitalCode().toString() : null);

            result = query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            throw new HISDataAccessException("UserManagementDAO::checkBackDateDayEnd()" + e);
        }
        // log.info("result::::" + result);
        return result != null ? result.toString() : "0";
    }

}
