package com.cdac.statewide.gatewayservice.filters;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import com.cdac.statewide.gatewayservice.util.JwtUtil;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.security.core.Authentication;

@Component
@Slf4j
public class JwtAuthenticationWebFilter implements WebFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationWebFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    private static final String BEARER = "Bearer ";
    private static final List<String> PUBLIC_PATHS = List.of(
            "/auth/login",
            "/auth/refresh",
            "/auth/register",
            "/auth/forgot"

    // "/auth/forgot.*"
    );

    @SuppressWarnings("null")
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();

        if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }
        log.info("Checking if path is public: {}", path);
        // Skip public APIs
        if (isPublicPath(path)) {
            log.info("Skipping JWT filter for public path: {}", path);
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith(BEARER)) {
            log.warn("Missing or invalid Authorization header for path {}", path);
            return unauthorized(exchange, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(BEARER.length());

        try {
            // Added this code Extract token type (access or refresh)
            String tokenType = jwtUtil.extractTokenType(token);
            // Reject requests using refresh token to access protected resources
            if ("refresh".equals(tokenType)) {
                return unauthorized(exchange, "Refresh token cannot access protected resources");
            }

            Claims claims = jwtUtil.validateToken(token);
            log.info("JWT valid | user={} | path={}", claims.getSubject(), path);

            List<String> roles = getRoles(claims);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    claims.getSubject(),
                    null,
                    roles.stream()
                            .map(r -> new SimpleGrantedAuthority("USER"))
                            .toList());

            ServerHttpRequest mutatedRequest = request.mutate()
                    .headers(h -> {
                        h.remove("X-User-Id");
                        h.remove("X-User-Roles");
                    })
                    .header("X-User-Id", claims.getSubject())
                    .header("X-User-Roles", String.join(",", roles))
                    .build();

            log.info("Authenticated user={} roles={}", claims.getSubject(), roles);

            return chain.filter(exchange.mutate().request(mutatedRequest).build())
                    .contextWrite(
                            ReactiveSecurityContextHolder.withAuthentication(authentication));

        } catch (Exception ex) {

            log.error("JWT validation failed for path {}", path);
            return unauthorized(exchange, "Invalid Or Expired JWT Token ");
        }
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    @SuppressWarnings("unchecked")
    private List<String> getRoles(Claims claims) {
        List<String> roles = claims.get("roles", List.class);
        return roles != null ? roles : List.of();
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {

        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // Create JSON body
        String body = String.format("{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"%s\"}", message);

        // Convert to DataBuffer
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

}
