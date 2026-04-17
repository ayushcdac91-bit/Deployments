package com.cdac.statewide.hiscommon.service.controller;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.cdac.statewide.hiscommon.service.dto.requestdto.MenuDataRequest;
import com.cdac.statewide.hiscommon.service.service.MenuDataService;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by Gaurav Kumar
 * 
 * This controller is responsible for fetching Menus Data.
 */
@RestController
@RequestMapping("/his/common-service")
@Slf4j
public class MenuDataController {
    private final MenuDataService menuDataService;

    public MenuDataController(MenuDataService menuDataService) {
        this.menuDataService = menuDataService;
    }

    @PostMapping("/getmenu")
    public ResponseEntity<Map<String, Object>> getAllMenuData(@RequestBody MenuDataRequest menuDataRequest) {
        log.info("Get Menu Data API called .");
        try {
            Map<String, Object> response = menuDataService.getMenuData(menuDataRequest);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("Get MenuData API failed", e);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Something went wrong during fetching menus data ", "message",
                            e.getMessage()));
        }
    }
}
