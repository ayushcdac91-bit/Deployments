package com.statewide.login.config;

import java.io.IOException;
import java.util.List;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.statewide.login.utils.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final RedisTemplate<String, String> redisTemplate;

    public JwtAuthenticationFilter(JwtService jwtService, RedisTemplate<String, String> redisTemplate) {
        this.jwtService = jwtService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        // Skip JWT check for public endpoints
        if (path.startsWith("/auth/login") || path.startsWith("/auth/forgot/")
                || path.startsWith("/auth/refresh")) {
            filterChain.doFilter(request, response);
            return;
        }
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"No Token Found invalid Authorization header\"}");

            return;
        }

        String token = authHeader.substring(7);

        try {

            // Validate token
            if (!jwtService.validateToken(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"Invalid or expired token\"}");
                return;
            }

            // 3. Check token type (access tokens only for protected resources)
            String tokenType = jwtService.extractTokenType(token);
            if (!"access".equals(tokenType)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Refresh token cannot access protected resources\"}");
                return;
            }
            // add this check token valid in redis or not
            // If the token is blacklisted deny access
            String accessJti = jwtService.getJti(token);
            String redisKey = "blacklist:access:" + accessJti;
            String status = redisTemplate.opsForValue().get(redisKey);

            if ("true".equals(status)) {

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Access token is blacklisted");
                return;
            }
            // Extract username
            String username = jwtService.extractSubject(token);
            log.info("UserName JwtFilterClass:::" + username);
            List<SimpleGrantedAuthority> authorities = jwtService.getAuthoritiesFromToken(token);
            log.info("Auth jwt filter  :; Authorities " + authorities);
            log.info("Authenticated user: {} with authorities: {}", username, authorities);

            // Create an authentication token
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
                    null, authorities);

            // Set authentication in Spring Security context
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        } catch (ExpiredJwtException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token expired, please login again");
            return;

        } catch (JwtException | IllegalArgumentException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    /*
     * private List<GrantedAuthority> getAuthorities(Claims claims) {
     * // Get roles/authorities from JWT claims
     * List<String> roles = claims.get("roles", List.class);
     * return
     * roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
     * }
     */
}
