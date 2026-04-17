package com.statewide.login.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.statewide.login.requestdto.ForgotPasswordRequest;
import com.statewide.login.service.ForgotPasswordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Created by Gaurav Kumar 02-12-2025
 * Responsible For Forgot Password functionality including handling OTP
 * generation
 * and password reset process.
 */
@RestController
@RequestMapping("/auth/forgot")
@Slf4j
public class ForgotPasswordController {

    private final ForgotPasswordService forgotPasswordService;

    public ForgotPasswordController(ForgotPasswordService forgotPasswordService) {
        this.forgotPasswordService = forgotPasswordService;
    }

    /**
     * Endpoint to fetch user details based on the username
     * 
     * @param request the request containing the username to fetch user details.
     * @return ResponseEntity with user details.
     */
    @PostMapping("/username")
    public ResponseEntity<?> fetchuserDetailsForgetPassword(@RequestBody ForgotPasswordRequest request) {
        log.info("Auth forgot username  request started.");
        try {
            return ResponseEntity.ok((forgotPasswordService.fetchUserDetailsByUserName(request.getVarUserName())));
        } catch (Exception e) {
            log.error("Unexpected error while forgot username details", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Something went wrong during forgot username details", "message",
                            e.getMessage()));
        }
    }

    /**
     * send an OTP to the mobile number associated with the username for
     * forgot password flow.
     * 
     * @param request the request containing the username and mobile number.
     * @return ResponseEntity with status and OTP details.
     */
    @PostMapping("/send-otp-mobile")
    public ResponseEntity<?> sendOTPMobile(@RequestBody ForgotPasswordRequest request) {
        log.info("Auth forgot send otp mobile request started.");
        try {
            return ResponseEntity.ok(
                    forgotPasswordService.sendMobileOtpForForgotPassword(request.getVarUserName(),
                            request.getVarMobileNumber()));
        } catch (Exception e) {
            log.error("Unexpected error while forgot send otp mobile", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Something went wrong during forgot send otp mobile", "message",
                            e.getMessage()));
        }
    }

    /**
     * verify OTP sent to either mobile or email for the forgot password
     * process.
     * 
     * @param request the request containing username, OTP, mobile number or email
     *                id for verification.
     * @return ResponseEntity with success or failure response for OTP verification.
     */
    @PostMapping("/verify-otp-mobile")
    public ResponseEntity<?> verifyMobileOtp(@RequestBody ForgotPasswordRequest request) {

        boolean verified = false;
        log.info("Auth forgot verify otp mobile request started.");
        if (request.getVarMobileNumber() != null) {
            verified = forgotPasswordService.verifyMobileOtpForgetPassword(
                    request.getVarUserName(),
                    request.getVarMobileNumber(),
                    request.getOtp(),
                    request.getVarEmailId());
        } else if (request.getVarEmailId() != null) {
            verified = forgotPasswordService.verifyMobileOtpForgetPassword(
                    request.getVarUserName(),
                    request.getVarEmailId(),
                    request.getOtp(), request.getVarMobileNumber());
        } else {
            return ResponseEntity.badRequest().body("Mobile number or email is required!");
        }
        if (verified) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "OTP Verified Successfully!");
            response.put("username", request.getVarUserName());
            log.info("verify otp response success");
            return ResponseEntity.ok(response);

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Wrong OTP!");
        }
    }

    /**
     * Endpoint to reset the password after successful OTP verification.
     * 
     * @param request the request containing username, new password, and confirm
     *                password.
     * @return ResponseEntity with the status of password reset.
     */
    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody ForgotPasswordRequest request) {
        log.info("Auth forgot reset change password request started.");
        try {
            return ResponseEntity.ok(
                    forgotPasswordService.resetForgottenPassword(request.getVarUserName(), request.getVarNewPassword(),
                            request.getVarConfirmPassword()));
        } catch (Exception e) {
            log.error("Unexpected error while forgot reset change password", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Something went wrong during forgot reset change password", "message",
                            e.getMessage()));
        }
    }

    /**
     * Endpoint to send an OTP to the email address associated with the username
     * 
     * @param request the request containing username and email id.
     * @return ResponseEntity with status and OTP details.
     */
    @PostMapping("/send-otp-email")
    public ResponseEntity<?> sendOTPEmail(@RequestBody ForgotPasswordRequest request) {
        log.info("Auth forgot send otp email request started.");
        try {
            return ResponseEntity.ok(
                    forgotPasswordService.sendEmailOtpForForgotPassword(request));
        } catch (Exception e) {
            log.error("Unexpected error while forgot send otp email", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Something went wrong during forgot send otp email", "message",
                            e.getMessage()));
        }
    }

}
