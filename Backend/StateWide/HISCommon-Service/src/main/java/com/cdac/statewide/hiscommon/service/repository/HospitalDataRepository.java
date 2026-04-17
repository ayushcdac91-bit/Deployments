package com.cdac.statewide.hiscommon.service.repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import com.cdac.statewide.hiscommon.service.entity.HospitalMasterVO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Repository
@Transactional
@Slf4j
public class HospitalDataRepository {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Fetching Hospital Detail
     * 
     * @param strMode_p
     * @param voHospital_p
     * @return List<HospitalMasterVO>
     * @throws Exception
     * @author
     */

    public List<HospitalMasterVO> getHospitalDetail(String strMode_p, HospitalMasterVO voHospital_p) {
        List<HospitalMasterVO> hospitalMasterData = new ArrayList<>();
        try {
            StoredProcedureQuery storedProcedure = entityManager
                    .createStoredProcedureQuery("pkg_usermgmt.proc_gblt_hospital_mst");

            storedProcedure.registerStoredProcedureParameter("p_mode", Integer.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("p_hospital_code", Integer.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("p_user_id", Integer.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("p_seat_id", Integer.class, ParameterMode.IN);

            storedProcedure.registerStoredProcedureParameter("err", String.class, ParameterMode.OUT);
            storedProcedure.registerStoredProcedureParameter("resultset", ResultSet.class, ParameterMode.REF_CURSOR);

            storedProcedure.setParameter("p_mode", Optional.ofNullable(Integer.parseInt(strMode_p)).orElse(null));
            storedProcedure.setParameter("p_hospital_code",
                    Optional.ofNullable(voHospital_p.getVarHospitalCode()).orElse(null));
            storedProcedure.setParameter("p_user_id", Optional.ofNullable(voHospital_p.getVarUserId()).orElse(null));
            storedProcedure.setParameter("p_seat_id",
                    Optional.ofNullable(voHospital_p.getVarUserSeatId()).orElse(null));

            storedProcedure.execute();

            String dbError = (String) storedProcedure.getOutputParameterValue("err");
            if (dbError != null && !dbError.isEmpty()) {
                log.error("Database Error : {} " + dbError);
                throw new RuntimeException("Database Error: " + dbError);
            }
            List<Object[]> results = storedProcedure.getResultList();

            for (Object[] row : results) {
                HospitalMasterVO hospitalMasterVO = new HospitalMasterVO();
                hospitalMasterVO.setVarHospitalCode(row[0] != null ? row[0].toString() : null);
                hospitalMasterVO.setVarHospitalName(row[1] != null ? row[1].toString() : null);
                hospitalMasterVO.setVarHospitalAddress1(row[2] != null ? row[2].toString() : null);
                hospitalMasterVO.setVarHospitalAddress2(row[3] != null ? row[3].toString() : null);
                hospitalMasterVO.setVarCity(row[4] != null ? row[4].toString() : null);
                hospitalMasterVO.setVarStateCode(row[5] != null ? row[5].toString() : null);
                hospitalMasterVO.setVarStateName(row[6] != null ? row[6].toString() : null);
                hospitalMasterVO.setVarPhone(row[7] != null ? row[7].toString() : null);
                hospitalMasterVO.setVarFax(row[8] != null ? row[8].toString() : null);
                hospitalMasterVO.setVarEmail(row[9] != null ? row[9].toString() : null);
                hospitalMasterVO.setVarContactPerson(row[10] != null ? row[10].toString() : null);
                hospitalMasterVO.setVarDistrictId(row[11] != null ? row[11].toString() : null);
                hospitalMasterVO.setVarDistrictName(row[12] != null ? row[12].toString() : null);
                hospitalMasterVO.setVarHL7Code(row[13] != null ? row[13].toString() : null);
                hospitalMasterVO.setVarHospitalShortName(row[14] != null ? row[14].toString() : null);
                hospitalMasterVO.setVarRemarks(row[15] != null ? row[15].toString() : null);
                hospitalMasterVO.setVarIsAssociated(row[16] != null ? row[16].toString() : null);
                hospitalMasterVO.setVarHospitalType(row[17] != null ? row[17].toString() : null);
                hospitalMasterVO.setVarHospitalCategory(row[18] != null ? row[18].toString() : null);
                hospitalMasterVO.setVarOrganizationType(row[19] != null ? row[19].toString() : null);
                hospitalMasterVO.setVarBusRouteNo(row[20] != null ? row[20].toString() : null);
                hospitalMasterVO.setVarBedCapacity(row[21] != null ? row[21].toString() : null);
                hospitalMasterVO.setVarWeekdaysTimings(row[22] != null ? row[22].toString() : null);
                hospitalMasterVO.setVarSaturdayTimings(row[23] != null ? row[23].toString() : null);
                hospitalMasterVO.setVarLunchBreak(row[24] != null ? row[24].toString() : null);
                hospitalMasterVO.setVarPANNo(row[25] != null ? row[25].toString() : null);
                hospitalMasterVO.setVarTANNo(row[26] != null ? row[26].toString() : null);
                hospitalMasterVO.setVarPinCode(row[27] != null ? row[27].toString() : null);
                hospitalMasterVO.setVarUserLicenceAllowed(row[28] != null ? row[28].toString() : null);
                hospitalMasterVO.setVarLanguageCode(row[29] != null ? row[29].toString() : null);
                hospitalMasterVO.setVarLanguageName(row[30] != null ? row[30].toString() : null);
                hospitalMasterVO.setVarLocalLangCode(row[31] != null ? row[31].toString() : null);
                hospitalMasterVO.setVarLocalLangName(row[32] != null ? row[32].toString() : null);
                hospitalMasterVO.setVarProjectCode(row[33] != null ? row[33].toString() : null);
                hospitalMasterData.add(hospitalMasterVO);
            }

        } catch (Exception e) {
            log.error("Database error while fetching Get Hospital Data {}", e);
            throw new RuntimeException("HospitalDataRepository.getHospitalDetail()::.executeProcedureByPosition"
                    + ") -> " + e.getMessage());
        }
        return hospitalMasterData;

    }

}
