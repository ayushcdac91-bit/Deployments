package com.statewide.login.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.statewide.login.requestdto.ChangeUserDetailsRequest;
import com.statewide.login.service.ChangeUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.AccessDeniedException;

@RestController
@RequestMapping("/auth")
@Slf4j
/**
 * Created by Gaurav Kumar on 12-12-2025
 * Responsible for handling user detail changes, including validation and saving
 * updates.
 * 
 * This controller provides endpoints for:
 * 1. Validate Password user details (change-userdetails).
 * 2. Saving updated user details (save-ChangeUserDetails).
 */
public class ChangeUserDetailsController {

    private final ChangeUserDetailsService changeUserDetailsService;

    public ChangeUserDetailsController(ChangeUserDetailsService changeUserDetailsService) {
        this.changeUserDetailsService = changeUserDetailsService;
    }

    @PostMapping("/change-userdetails")
    public ResponseEntity<?> changeUserDetails(@RequestBody ChangeUserDetailsRequest changeUserDetailsRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("User not authenticated");
        }
        String username = authentication.getName(); // username from JWT

        log.info("User from JWT: {}", username);
        log.info("Auth change user details request started.");
        try {
            Map<String, Object> response = changeUserDetailsService.validatePassword(username,
                    changeUserDetailsRequest);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Unexpected error while change user details", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Something went wrong during change user  details", "message",
                            e.getMessage()));
        }

    }

    @PostMapping("/save-ChangeUserDetails")
    public ResponseEntity<?> saveChangeUserDetail(@RequestBody ChangeUserDetailsRequest changeUserDetailsRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("User not authenticated");
        }

        String username = authentication.getName();
        log.info("UserName from JWT: {}", username);
        log.info("Auth save change user details request started.");
        try {
            Map<String, Object> response = changeUserDetailsService.saveChangeUserDetails(username,
                    changeUserDetailsRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Unexpected error while save change user details", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Something went wrong during save change user  details", "message",
                            e.getMessage()));
        }
    }

}
