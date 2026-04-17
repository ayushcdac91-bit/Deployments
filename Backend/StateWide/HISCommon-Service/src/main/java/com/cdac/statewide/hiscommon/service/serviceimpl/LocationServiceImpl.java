package com.cdac.statewide.hiscommon.service.serviceimpl;

import com.cdac.statewide.hiscommon.service.dto.responsedto.CountryResponseDTO;
import com.cdac.statewide.hiscommon.service.dto.responsedto.DistrictResponseDTO;
import com.cdac.statewide.hiscommon.service.dto.responsedto.StateResponseDTO;
import com.cdac.statewide.hiscommon.service.exception.HISDataAccessException;
import com.cdac.statewide.hiscommon.service.exception.HisRecordNotFoundException;
import com.cdac.statewide.hiscommon.service.repository.LocationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.cdac.statewide.hiscommon.service.service.LocationService;

import java.util.List;

@Slf4j
@Service
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public List<CountryResponseDTO> getCountries() {
        log.info("Fetching country list...");
        try {
            List<CountryResponseDTO> countries = locationRepository.getCountries();

            if (countries == null || countries.isEmpty()) {
                log.info("No countries found.");
            } else {
                log.info("Fetched {} countries.", countries.size());
            }
            return countries;
        } catch (HisRecordNotFoundException | HISDataAccessException e) {
            log.info("Error fetching countries: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.info("Unexpected error while fetching countries.", e);
            throw new HISDataAccessException("Unexpected error while fetching countries: " + e.getMessage());
        }
    }

    @Override
    public List<StateResponseDTO> getStates(String countryId) {
        log.info("Fetching states for countryId: {}", countryId);
        try {
            List<StateResponseDTO> states = locationRepository.getStates(countryId);
            if (states == null || states.isEmpty()) {
                log.info("No states found for countryId: {}", countryId);
            } else {
                log.info("Fetched {} states for countryId: {}", states.size(), countryId);
            }
            return states;
        } catch (HisRecordNotFoundException | HISDataAccessException e) {
            log.info("Error fetching states for {}: {}", countryId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.info("Unexpected error while fetching states for {}.", countryId, e);
            throw new HISDataAccessException("Unexpected error while fetching states: " + e.getMessage());
        }
    }

    @Override
    public List<DistrictResponseDTO> getDistricts(String stateCode) {
        log.info("Fetching districts for stateCode: {}", stateCode);
        try {
            List<DistrictResponseDTO> districts = locationRepository.getDistricts(stateCode);
            if (districts == null || districts.isEmpty()) {
                log.info("No districts found for stateCode: {}", stateCode);
            } else {
                log.info("Fetched {} districts for stateCode: {}", districts.size(), stateCode);
            }
            return districts;
        } catch (HisRecordNotFoundException | HISDataAccessException e) {
            log.info("Error fetching districts for {}: {}", stateCode, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.info("Unexpected error while fetching districts for {}.", stateCode, e);
            throw new HISDataAccessException("Unexpected error while fetching districts: " + e.getMessage());
        }
    }

}

