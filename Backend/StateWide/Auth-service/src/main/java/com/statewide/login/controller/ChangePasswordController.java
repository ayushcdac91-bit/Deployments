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
import com.statewide.login.requestdto.ChangePasswordRequest;
import com.statewide.login.service.ChangePasswordService;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Gaurav Kumar 08-12-2025
 * Responsible For Change Password
 */
@RestController
@RequestMapping("/auth")
@Slf4j
public class ChangePasswordController {

    private final ChangePasswordService changePasswordService;

    public ChangePasswordController(ChangePasswordService changePasswordService) {
        this.changePasswordService = changePasswordService;
    }

    /**
     * Handles the POST request for changing the user's password.
     * 
     * @param request Contains the new password information for the user.
     * @return ResponseEntity with the result of the password change operation.
     * @throws AccessDeniedException if the user is not authenticated.
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("User not authenticated");
        }

        String varUserName = authentication.getName(); // username from JWT
        log.info("username  {}", varUserName);

        log.info("Auth Change Password API request started.");
        try {
            return ResponseEntity.ok((changePasswordService.changePassword(varUserName, request)));
        } catch (Exception e) {
            log.error("Unexpected error while change password", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Something went wrong during change password", "message", e.getMessage()));
        }
    }

}
