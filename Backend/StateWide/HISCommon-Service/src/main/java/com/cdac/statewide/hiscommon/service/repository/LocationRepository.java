package com.cdac.statewide.hiscommon.service.repository;

import com.cdac.statewide.hiscommon.service.dto.responsedto.CountryResponseDTO;
import com.cdac.statewide.hiscommon.service.dto.responsedto.StateResponseDTO;
import com.cdac.statewide.hiscommon.service.dto.responsedto.DistrictResponseDTO;
import com.cdac.statewide.hiscommon.service.exception.HISDataAccessException;
import com.cdac.statewide.hiscommon.service.exception.HisRecordNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for fetching Country, State, and District data from PostgreSQL using stored procedures.
 * Fully handles REF_CURSORs and transaction management.
 */
@Slf4j
@Repository
public class LocationRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Stored procedure names
    private static final String PROC_COUNTRY = "CALL pkg_gbl_view.proc_gblt_country_mst(?, ?, ?)";
    private static final String PROC_STATE = "CALL pkg_gbl_view.proc_gblt_state_mst(?, ?, ?, ?)";
    private static final String PROC_DISTRICT = "CALL pkg_gbl_view.proc_gblt_district_mst(?, ?, ?, ?)";

    // -----------------------------------------------------------------------
    //  Get Country List
    // -----------------------------------------------------------------------
    public List<CountryResponseDTO> getCountries() {
        log.info("[LocationRepository] Fetching country list from DB...");
        List<CountryResponseDTO> countries = new ArrayList<>();

        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            connection.setAutoCommit(false);

            try (CallableStatement cs = connection.prepareCall(PROC_COUNTRY)) {
                cs.setString(1, "1"); // p_modeval
                cs.registerOutParameter(2, Types.VARCHAR); // err
                cs.registerOutParameter(3, Types.REF_CURSOR); // resultset

                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(3)) {
                    if (rs == null || !rs.isBeforeFirst()) {
                        throw new HisRecordNotFoundException("No Country Found");
                    }

                    while (rs.next()) {
                        countries.add(new CountryResponseDTO(
                                rs.getString("GSTR_COUNTRY_CODE"),
                                rs.getString("GSTR_COUNTRY_NAME")
                        ));
                    }
                }

                String err = cs.getString(2);
                if (err != null && !err.isEmpty()) {
                    throw new HISDataAccessException("PAT_400: Invalid input - " + err);
                }

                connection.commit();
                log.info("[LocationRepository] Successfully fetched {} countries.", countries.size());
                return countries;

            } catch (HisRecordNotFoundException | HISDataAccessException e) {
                connection.rollback();
                log.warn("[LocationRepository] Business/validation error (Country): {}", e.getMessage());
                throw e;

            } catch (Exception e) {
                connection.rollback();
                log.error("[LocationRepository] Unexpected SQL error (Country): {}", e.getMessage(), e);
                throw new HISDataAccessException("PAT_500: Internal server error - " + e.getMessage(), e);

            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            log.error("[LocationRepository] Database connection failure (Country): {}", e.getMessage(), e);
            throw new HISDataAccessException("PAT_500: Database connection error - " + e.getMessage(), e);
        }
    }

    // -----------------------------------------------------------------------
    //  Get State List (based on Country)
    // -----------------------------------------------------------------------
    public List<StateResponseDTO> getStates(String countryId) {
        String finalCountryId = (countryId == null || countryId.trim().isEmpty()) ? "IND" : countryId.trim();
        log.info("[LocationRepository] Fetching states for countryId: {}", finalCountryId);

        List<StateResponseDTO> states = new ArrayList<>();

        try (Connection conn = jdbcTemplate.getDataSource().getConnection()) {
            conn.setAutoCommit(false);

            try (CallableStatement cs = conn.prepareCall(PROC_STATE)) {
                cs.setString(1, "1"); // p_modeval
                cs.setString(2, finalCountryId); // p_countryCode
                cs.registerOutParameter(3, Types.VARCHAR); // err
                cs.registerOutParameter(4, Types.REF_CURSOR); // resultset

                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(4)) {
                    if (rs == null || !rs.isBeforeFirst()) {
                        throw new HisRecordNotFoundException("No State Found");
                    }

                    while (rs.next()) {
                        states.add(new StateResponseDTO(
                                rs.getString("GNUM_STATE_CODE"),
                                rs.getString("GSTR_STATE_NAME")
                        ));
                    }
                }

                String err = cs.getString(3);
                if (err != null && !err.isEmpty()) {
                    throw new HISDataAccessException("PAT_400: Invalid input - " + err);
                }

                conn.commit();
                log.info("[LocationRepository] Successfully fetched {} states for country {}.", states.size(), finalCountryId);
                return states;

            } catch (HisRecordNotFoundException | HISDataAccessException e) {
                conn.rollback();
                log.warn("[LocationRepository] Business error (State): {}", e.getMessage());
                throw e;

            } catch (Exception e) {
                conn.rollback();
                log.error("[LocationRepository] Unexpected SQL error (State): {}", e.getMessage(), e);
                throw new HISDataAccessException("PAT_500: Internal server error - " + e.getMessage(), e);

            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            log.error("[LocationRepository] Database connection failure (State): {}", e.getMessage(), e);
            throw new HISDataAccessException("PAT_500: Database connection error - " + e.getMessage(), e);
        }
    }

    // -----------------------------------------------------------------------
    //  Get District List (based on State)
    // -----------------------------------------------------------------------
    public List<DistrictResponseDTO> getDistricts(String stateCode) {
        String finalStateCode = (stateCode == null || stateCode.trim().isEmpty()) ? "" : stateCode.trim();
        log.info("[LocationRepository] Fetching districts for stateCode: {}", finalStateCode);

        List<DistrictResponseDTO> districts = new ArrayList<>();

        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            connection.setAutoCommit(false);

            try (CallableStatement cs = connection.prepareCall(PROC_DISTRICT)) {
                cs.setString(1, "1"); // p_modeVal
                cs.setString(2, finalStateCode); // p_stateCode
                cs.registerOutParameter(3, Types.VARCHAR); // err
                cs.registerOutParameter(4, Types.REF_CURSOR); // resultset

                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(4)) {
                    if (rs == null || !rs.isBeforeFirst()) {
                        throw new HisRecordNotFoundException("No District Found");
                    }

                    while (rs.next()) {
                        districts.add(new DistrictResponseDTO(
                                rs.getString("GNUM_DISTRICT_CODE"),
                                rs.getString("GSTR_DISTRICT_NAME")
                        ));
                    }
                }

                String err = cs.getString(3);
                if (err != null && !err.isEmpty()) {
                    throw new HISDataAccessException("PAT_400: Invalid input - " + err);
                }

                connection.commit();
                log.info("[LocationRepository] Successfully fetched {} districts for state {}.", districts.size(), finalStateCode);
                return districts;

            } catch (HisRecordNotFoundException | HISDataAccessException e) {
                connection.rollback();
                log.warn("[LocationRepository] Business error (District): {}", e.getMessage());
                throw e;

            } catch (Exception e) {
                connection.rollback();
                log.error("[LocationRepository] Unexpected SQL error (District): {}", e.getMessage(), e);
                throw new HISDataAccessException("PAT_500: Internal server error - " + e.getMessage(), e);

            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            log.error("[LocationRepository] Database connection failure (District): {}", e.getMessage(), e);
            throw new HISDataAccessException("PAT_500: Database connection error - " + e.getMessage(), e);
        }
    }
}
