package com.cdac.statewide.hiscommon.service.controller;

import com.cdac.statewide.hiscommon.service.dto.MasterResponse;
import com.cdac.statewide.hiscommon.service.dto.responsedto.CountryResponseDTO;
import com.cdac.statewide.hiscommon.service.dto.responsedto.DistrictResponseDTO;
import com.cdac.statewide.hiscommon.service.dto.responsedto.StateResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.cdac.statewide.hiscommon.service.service.LocationService;

import java.util.List;

/**
 * Controller for fetching location master data such as
 * countries, states, and districts.
 *
 * @author Priyanka Thakur
 */
@RestController
@Slf4j
@RequestMapping("/his/common-service")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    /**
     * API: Get Country List
     * Method: POST
     * Endpoint: /his/common-service/getCountry
     * Description: Fetches all countries from the database using a stored procedure.
     */
    @PostMapping("/getCountry")
    public MasterResponse<List<CountryResponseDTO>> getCountryList() {
        log.info("Inside getCountryList API");
        List<CountryResponseDTO> countries = locationService.getCountries();

        if (countries == null || countries.isEmpty()) {
            return new MasterResponse<>(false, "No countries found in the database.", HttpStatus.NO_CONTENT.value(), null);
        }

        return new MasterResponse<>(true, "Country list fetched successfully.", HttpStatus.OK.value(), countries);
    }

    /**
     * API: Get State List
     * Method: POST
     * Endpoint: /his/common-service/getState/{countryId}
     * Description: Fetches all states for a given country from the database.
     */
    @PostMapping("/getState/{countryId}")
    public MasterResponse<List<StateResponseDTO>> getStateList(@PathVariable String countryId) {
        log.info("Inside getStateList API for countryId: {}", countryId);
        List<StateResponseDTO> states = locationService.getStates(countryId);

        if (states == null || states.isEmpty()) {
            return new MasterResponse<>(false, "No states found for the given country.", HttpStatus.NO_CONTENT.value(), null);
        }

        return new MasterResponse<>(true, "State list fetched successfully.", HttpStatus.OK.value(), states);
    }


    /**
     * API: Get District List based on stateCode
     * Method: POST
     * Endpoint: /his/common-service/getDistrict/{stateCode}
     * Description: Fetches all districts for a given state from the database.
     */
    @PostMapping("/getDistrict/{stateCode}")
    public MasterResponse<List<DistrictResponseDTO>> getDistrictList(@PathVariable String stateCode) {
        log.info("Inside getDistrictList API for stateCode: {}", stateCode);

        List<DistrictResponseDTO> districts = locationService.getDistricts(stateCode);

        if (districts == null || districts.isEmpty()) {
            return new MasterResponse<>(false, "No districts found.", HttpStatus.NO_CONTENT.value(), null);
        }

        return new MasterResponse<>(true, "District list fetched successfully.", HttpStatus.OK.value(), districts);
    }
}
