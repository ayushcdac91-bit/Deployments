package com.statewide.login.controller;

import org.springframework.web.bind.annotation.RestController;

import com.statewide.login.entity.UserMasterVO;
import com.statewide.login.requestdto.LoginUserRequestDTO;
import com.statewide.login.service.LoginService;
import com.statewide.login.utils.EncryptionUtil;
import com.statewide.login.utils.JwtService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.http.auth.InvalidCredentialsException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/auth")
@Slf4j

/**
 * Created by Gaurav Kumar 02-12-2025
 * 
 * LoginController handles user authentication by generating access and refresh
 * tokens.
 * It also facilitates refreshing access tokens using a refresh token stored in
 * Redis.
 */

public class LoginController {

    private final LoginService loginService;
    private final JwtService jwtService;
    private final RedisTemplate<String, String> redisTemplate;

    public LoginController(LoginService loginService, JwtService jwtService,
            RedisTemplate<String, String> redisTemplate) {
        this.loginService = loginService;
        this.jwtService = jwtService;
        this.redisTemplate = redisTemplate;
    }

    /**
     * Handle user login and generate JWT access and refresh tokens.
     * 
     * @param request the login request containing username and password.
     * @return ResponseEntity with the user data and message ,
     *         including access and refresh
     *         tokens.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginUserRequestDTO request,
            HttpServletResponse responseHttp) throws Exception {
        log.info("Auth Login API request started.");
        HttpHeaders headers = new HttpHeaders();
        try {
            Map<String, Object> response = loginService.login(request);

            if (response.containsKey("jwtToken")) {

                // Encrypted Access Token
                String accessToken = (String) response.get("jwtToken");
                @SuppressWarnings("unused")
                String encryptedAccessToken = EncryptionUtil.encrypt(accessToken);

                headers.set(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + response.get("jwtToken"));

                String refreshToken = jwtService.generateRefreshToken(request.getVarUserName());
                // headers.set("Refresh-Token", refreshToken);
                response.put("Refresh-Token", refreshToken);
                // Encrypted Refresh Token and set in header
                // headers.set("Refresh-Token", EncryptionUtil.encrypt(refreshToken));
                String refreshJti = jwtService.getJti(refreshToken);
                // Set the TTL to 15 minutes
                long ttlInMinutes = 15;
                // Store the refresh token with the username in Redis for 15 minutes
                redisTemplate.opsForValue().set("refresh:" + refreshJti, request.getVarUserName(), ttlInMinutes,
                        TimeUnit.MINUTES);
                response.remove("jwtToken");
            }
            return ResponseEntity.ok().headers(headers).body(response);
        } catch (InvalidCredentialsException e) {
            log.error("Unexpected error  Invalid credentials", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials", "message", e.getMessage()));

        } catch (Exception e) {
            log.error("Unexpected error while login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Something went wrong during login", "message", e.getMessage()));
        }

    }

    /**
     * Created by Gaurav Kumar 07-12-2025
     * Handle refresh token functionality by validating the provided refresh token,
     * and generating a new one if it's valid.
     * 
     * @param refreshToken The refresh token sent in the request header.
     * @return ResponseEntity with a success message if the token is valid and
     *         refreshed,
     *         or an error message if the token is invalid or expired.
     */

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshToken(@RequestHeader("Refresh-Token") String refreshToken)
            throws Exception {

        log.info("Auth Refresh API request started.");
        HttpHeaders headers = new HttpHeaders();
        Map<String, Object> response = new HashMap<>();
        // log.info(("refreshToken in auth/refreshapi ---" + refreshToken));
        try {

            // Check key present in redis for refresh token from the refresh token
            // Construct the Redis key using the refresh JTI
            String refreshJti = jwtService.getJti(refreshToken);
            String redisKey = "refresh:" + refreshJti;
            if (!Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
                log.warn("Refresh token not found or expired in Redis. Token JTI: {}", refreshJti);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired refresh token Please Login Again", "message",
                                "The refresh token does not exist or has expired. You may need to reauthenticate."));
            }

            // Validate JWT refresh token
            if (!jwtService.validateToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired refresh token Please Login Again"));

            }

            // Delete old refresh token from Redis

            Boolean deletedRefreshToken = redisTemplate.delete("refresh:" + refreshJti);
            if (Boolean.TRUE.equals(deletedRefreshToken)) {
                log.info("Refresh token [{}] deleted from Redis", refreshJti);
            } else {
                log.debug("Refresh token [{}] not found or already expired", refreshJti);
            }
            // Generate New Refresh Token for rotation
            String username = jwtService.extractUsername(refreshToken);
            // Added this code for genearte new Access token
            String newAccessToken = jwtService.generateAccessToken(username);
            String newrefreshToken = jwtService.generateRefreshToken(username);
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken);
            response.put("Refresh-Token", newrefreshToken);
            response.put("message", "Refresh token generated successfully");
            // headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + newrefreshToken);

            String refreshGetJti = jwtService.getJti(newrefreshToken);
            // Set the TTL to 15 minutes
            long ttlInMinutes = 15;
            // Store the refresh token with the username in Redis for 15 minutes
            redisTemplate.opsForValue().set("refresh:" + refreshGetJti, username, ttlInMinutes, TimeUnit.MINUTES);

            return ResponseEntity.ok().headers(headers)
                    .body(response);

        } catch (ExpiredJwtException e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "error", "refresh_token_expired",
                            "message", "Refresh token has expired. Please login again."));

        } catch (JwtException e) {
            log.error("Unexpected error while invalid refresh token", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "error", "invalid_refresh_token",
                            "message", "Refresh token is invalid. Please login again."));

        } catch (Exception e) {
            log.error("Unexpected error while refreshing token", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "internal_server_error",
                            "message", "Unable to refresh token at this time."));
        }
    }

    /**
     * FETCH USER Details API for internally api calling .
     * 
     */
    @GetMapping("/user/details")
    public UserMasterVO getUserDetails(@RequestParam String username) {
        // Calls repository internally
        log.info("Auth User Details API {} ", username);
        return loginService.fetchUserDetails("1", username);
    }
}
