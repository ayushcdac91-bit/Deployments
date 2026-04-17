package com.cdac.statewide.hiscommon.service.repository;

import com.cdac.statewide.hiscommon.service.dto.responsedto.CasteResponseDTO;
import com.cdac.statewide.hiscommon.service.dto.responsedto.MaritalStatusResponseDTO;
import com.cdac.statewide.hiscommon.service.dto.responsedto.OccupationResponseDTO;
import com.cdac.statewide.hiscommon.service.dto.responsedto.ReligionResponseDTO;
import com.cdac.statewide.hiscommon.service.exception.HISDataAccessException;
import com.cdac.statewide.hiscommon.service.exception.HisRecordNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class DemographicRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String PROC_MARITAL = "CALL pkg_gbl_view.proc_gblt_marital_status_mst(?, ?, ?)";
    private static final String PROC_RELIGION = "CALL pkg_gbl_view.proc_gblt_religion_mst(?, ?, ?)";
    private static final String PROC_CASTE = "CALL pkg_gbl_view.proc_gblt_caste_mst(?, ?, ?)";
    private static final String PROC_OCCUPATION = "CALL pkg_gbl_view.proc_gblt_occupation_mst(?, ?, ?, ?)";

    /**
     * Fetch marital status list from database using stored procedure.
     */
    public List<MaritalStatusResponseDTO> getMaritalStatus() {
        log.info("[DemographicRepository] Fetching marital status list...");

        List<MaritalStatusResponseDTO> maritalList = new ArrayList<>();

        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            connection.setAutoCommit(false);

            try (CallableStatement cs = connection.prepareCall(PROC_MARITAL)) {
                cs.setString(1, "1"); // p_modeVal (static)
                cs.registerOutParameter(2, Types.VARCHAR); // err
                cs.registerOutParameter(3, Types.REF_CURSOR); // resultset

                log.debug("[DemographicRepository] Executing stored procedure: {}", PROC_MARITAL);
                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(3)) {
                    if (rs == null || !rs.isBeforeFirst()) {
                        log.warn("[DemographicRepository] No marital status found.");
                        throw new HisRecordNotFoundException("No Marital Status Found");
                    }

                    while (rs.next()) {
                        maritalList.add(new MaritalStatusResponseDTO(
                                rs.getString("GNUM_MARITAL_STATUS_CODE"),
                                rs.getString("GSTR_MARITAL_STATUS")
                        ));
                    }
                }

                String err = cs.getString(2);
                if (err != null && !err.isEmpty()) {
                    log.error("[DemographicRepository] Procedure returned error: {}", err);
                    throw new HISDataAccessException("PAT_400: Invalid input - " + err);
                }

                connection.commit();
                log.info("[DemographicRepository] Successfully fetched {} marital status records.", maritalList.size());
                return maritalList;

            } catch (HisRecordNotFoundException | HISDataAccessException e) {
                connection.rollback();
                log.warn("[DemographicRepository] Business error: {}", e.getMessage());
                throw e;

            } catch (Exception e) {
                connection.rollback();
                log.error("[DemographicRepository] Unexpected error: {}", e.getMessage(), e);
                throw new HISDataAccessException("PAT_500: Internal server error - " + e.getMessage(), e);

            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            log.error("[DemographicRepository] DB connection failure: {}", e.getMessage(), e);
            throw new HISDataAccessException("PAT_500: Database connection error - " + e.getMessage(), e);
        }
    }

    /**
     * Fetch religion list
     */
    public List<ReligionResponseDTO> getReligion() {
        log.info("[DemographicRepository] Fetching religion list...");
        List<ReligionResponseDTO> religionList = new ArrayList<>();

        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            connection.setAutoCommit(false);

            try (CallableStatement cs = connection.prepareCall(PROC_RELIGION)) {
                cs.setString(1, "1");
                cs.registerOutParameter(2, Types.VARCHAR);
                cs.registerOutParameter(3, Types.REF_CURSOR);
                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(3)) {
                    if (rs == null || !rs.isBeforeFirst())
                        throw new HisRecordNotFoundException("No Religion Found");

                    while (rs.next()) {
                        religionList.add(new ReligionResponseDTO(
                                rs.getString("GNUM_RELIGION_CODE"),
                                rs.getString("GSTR_RELIGION_NAME")
                        ));
                    }
                }

                String err = cs.getString(2);
                if (err != null && !err.isEmpty())
                    throw new HISDataAccessException("PAT_400: Invalid input - " + err);

                connection.commit();
                log.info("[DemographicRepository] Fetched {} religion records.", religionList.size());
                return religionList;

            } catch (HisRecordNotFoundException | HISDataAccessException e) {
                connection.rollback();
                throw e;
            } catch (Exception e) {
                connection.rollback();
                throw new HISDataAccessException("PAT_500: Internal error - " + e.getMessage(), e);
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new HISDataAccessException("PAT_500: Database error - " + e.getMessage(), e);
        }
    }

    /**
     * Fetch patient caste list
     */
    public List<CasteResponseDTO> getPatientCaste() {
        log.info("[DemographicRepository] Fetching patient caste list...");
        List<CasteResponseDTO> casteList = new ArrayList<>();

        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            connection.setAutoCommit(false);

            try (CallableStatement cs = connection.prepareCall(PROC_CASTE)) {
                cs.setString(1, "1"); // p_modeVal (static)
                cs.registerOutParameter(2, Types.VARCHAR); // err
                cs.registerOutParameter(3, Types.REF_CURSOR); // resultset

                log.debug("[DemographicRepository] Executing procedure: {}", PROC_CASTE);
                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(3)) {
                    if (rs == null || !rs.isBeforeFirst()) {
                        log.warn("[DemographicRepository] No caste data found.");
                        throw new HisRecordNotFoundException("No Patient Caste Found");
                    }

                    while (rs.next()) {
                        casteList.add(new CasteResponseDTO(
                                rs.getString("GSTR_CASTE_CODE"),
                                rs.getString("GSTR_CASTE_NAME")
                        ));
                    }
                }

                String err = cs.getString(2);
                if (err != null && !err.isEmpty()) {
                    log.error("[DemographicRepository] Stored procedure returned error: {}", err);
                    throw new HISDataAccessException("PAT_400: Invalid input - " + err);
                }

                connection.commit();
                log.info("[DemographicRepository] Successfully fetched {} patient castes.", casteList.size());
                return casteList;

            } catch (HisRecordNotFoundException | HISDataAccessException e) {
                connection.rollback();
                log.warn("[DemographicRepository] Business/Validation error: {}", e.getMessage());
                throw e;

            } catch (Exception e) {
                connection.rollback();
                log.error("[DemographicRepository] Unexpected error: {}", e.getMessage(), e);
                throw new HISDataAccessException("PAT_500: Internal server error - " + e.getMessage(), e);

            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            log.error("[DemographicRepository] Database connection error: {}", e.getMessage(), e);
            throw new HISDataAccessException("PAT_500: Database connection error - " + e.getMessage(), e);
        }
    }

    /**
     * Fetch patient occupation list
     */
    public List<OccupationResponseDTO> getPatientOccupation() {
        log.info("[DemographicRepository] Fetching patient occupation list...");
        List<OccupationResponseDTO> occupationList = new ArrayList<>();

        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            connection.setAutoCommit(false);

            try (CallableStatement cs = connection.prepareCall(PROC_OCCUPATION)) {
                cs.setString(1, "3"); // p_mode (static)
                cs.setNull(2, Types.VARCHAR); // p_occupationid (NULL)
                cs.registerOutParameter(3, Types.VARCHAR); // err
                cs.registerOutParameter(4, Types.REF_CURSOR); // resultset

                log.debug("[DemographicRepository] Executing procedure: {}", PROC_OCCUPATION);
                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(4)) {
                    if (rs == null || !rs.isBeforeFirst()) {
                        log.warn("[DemographicRepository] No occupation data found.");
                        throw new HisRecordNotFoundException("No Patient Occupation Found");
                    }

                    while (rs.next()) {
                        String code = rs.getString("GNUM_OCCUPATION_CODE");
                        String name = rs.getString(2); // Fallback to 2nd column if alias missing
                        occupationList.add(new OccupationResponseDTO(code, name));
                    }
                }

                String err = cs.getString(3);
                if (err != null && !err.isEmpty()) {
                    log.error("[DemographicRepository] Stored procedure returned error: {}", err);
                    throw new HISDataAccessException("PAT_400: Invalid input - " + err);
                }

                connection.commit();
                log.info("[DemographicRepository] Successfully fetched {} patient occupations.", occupationList.size());
                return occupationList;

            } catch (HisRecordNotFoundException | HISDataAccessException e) {
                connection.rollback();
                log.warn("[DemographicRepository] Business/Validation error: {}", e.getMessage());
                throw e;

            } catch (Exception e) {
                connection.rollback();
                log.error("[DemographicRepository] Unexpected error: {}", e.getMessage(), e);
                throw new HISDataAccessException("PAT_500: Internal server error - " + e.getMessage(), e);

            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            log.error("[DemographicRepository] Database connection error: {}", e.getMessage(), e);
            throw new HISDataAccessException("PAT_500: Database connection error - " + e.getMessage(), e);
        }
    }
}
