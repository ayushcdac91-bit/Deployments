package com.statewide.login.controller;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.statewide.login.requestdto.UserLogDateRequest;
import com.statewide.login.service.UserLogDetailService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/auth")
@Slf4j
/**
 * Created by Gaurav Kumar on 26-12-2025
 * Responsible for fetching user log details.
 * This controller retrieves the user log details and all userlog details
 * fromdate and to date for the authenticated user.
 */
public class UserLogDetailController {

    private final UserLogDetailService userLogDetailService;

    public UserLogDetailController(UserLogDetailService userLogDetailService) {
        this.userLogDetailService = userLogDetailService;
    }

    @PostMapping("/userlogdetails")
    public ResponseEntity<Map<String, Object>> userLogDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("User not authenticated");
        }
        String username = authentication.getName(); // username from JWT
        log.info("Auth user logs details request started.");
        try {
            Map<String, Object> response = userLogDetailService.initUserLogDetails(username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Unexpected error while user logs details", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error",
                            "Something went wrong during fetching User Log Details ",
                            "message",
                            e.getMessage()));
        }
    }

    @PostMapping("/alluserlogdetails")
    public ResponseEntity<Map<String, Object>> allUserLogDetails(@RequestBody UserLogDateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("User not authenticated");
        }
        String username = authentication.getName();
        log.info("Auth user logs details request started.");
        try {
            Map<String, Object> response = userLogDetailService.allUserLogDetails(username, request.getFromDate(),
                    request.getToDate(), request.getPage(),
                    request.getSize());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Unexpected error while fetching User Log Details  between the fromDate  and toDate", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error",
                            "Something went wrong during fetching User Log Details  between the fromDate  and toDate",
                            "message",
                            e.getMessage()));
        }
    }

}
