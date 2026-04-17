package com.statewide.login.controller;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.statewide.login.service.LogoutService;
import com.statewide.login.utils.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Gaurav KumarDated on 31-12-2025
 * 
 * Responsible for managing user logout functionality.
 * This includes blacklisting the access token validates the tokens, deletes
 * refresh tokens from Redis,
 * and blacklists the access token so that it cannot be used again for
 * authentication .
 * 
 * 
 */
@RestController
@RequestMapping("/auth")
@Slf4j
public class LogoutController {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtService jwtService;
    private final LogoutService logoutService;

    public LogoutController(RedisTemplate<String, String> redisTemplate, JwtService jwtService,
            LogoutService logoutService) {
        this.redisTemplate = redisTemplate;
        this.jwtService = jwtService;
        this.logoutService = logoutService;
    }

    @PostMapping("/logoutLogin")
    public ResponseEntity<Map<String, Object>> logout(@RequestHeader("Authorization") String authHeader,
            @RequestHeader("Refresh-Token") String refreshToken, HttpServletResponse response) {

        log.info("Auth  Logout API  request started.");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Authorization header missing or invalid"));
        }
        String accessToken = authHeader.substring(7);
        // log.info("Refresh Token {} ", refreshToken);
        // Remove refresh token from Redis (if stored)
        if (refreshToken != null && !refreshToken.isEmpty()) {
            try {
                if (jwtService.validateToken(refreshToken)) {
                    String refreshJti = jwtService.getJti(refreshToken);
                    Boolean deletedRefresh = redisTemplate.delete("refresh:" + refreshJti);
                    if (Boolean.TRUE.equals(deletedRefresh)) {
                        log.info("Refresh token [{}] deleted from Redis", refreshJti);
                    } else {
                        log.debug("Refresh token [{}] not found or already expired", refreshJti);
                    }

                }
            } catch (Exception e) {

                log.error("Invalid refresh token during logout: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "Invalid refresh token during logout", "message", e.getMessage()));
            }

        }

        // Blacklist access token in Redis

        if (accessToken != null && !accessToken.isEmpty()) {
            try {
                if (jwtService.validateToken(accessToken)) {
                    long remainingTtl = jwtService.getExpiration(accessToken).getTime() - System.currentTimeMillis();
                    String accessJti = jwtService.getJti(accessToken);
                    if (remainingTtl > 0) {

                        redisTemplate.opsForValue().set(
                                "blacklist:access:" + accessJti,
                                "true",
                                remainingTtl,
                                TimeUnit.MILLISECONDS);
                    }
                }
            } catch (Exception e) {
                log.error("Invalid access token during logout: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "Invalid access token during logout", "message", e.getMessage()));
            }
        }
        String userName = jwtService.extractSubject(accessToken);
        log.info(" User Name {} ", userName);
        try {
            // Logout DB update Logs for Logout
            logoutService.logoutLogDetails(userName);
        } catch (Exception e) {
            log.error("Logout DB update failed for user {}", userName, e);
        }

        return ResponseEntity.ok().body(Map.of("message", "Logout successful"));

    }
}
