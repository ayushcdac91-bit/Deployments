package com.statewide.login.utils;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Component
public class JwtService {

        public final String subjectSeperator = "HIS";

        public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";
        private static final long EXPIRATION_TIME = 30 * 60 * 1000; // 30 minutes in milliseconds
        // private static final long REFRESH_TOKEN_EXP = 1 * 24 * 60 * 60 * 1000 1
        // daysin ms
        // private static final long REFRESH_TOKEN_EXP = 1 * 60 * 60 * 1000; // 1 hour
        // in ms

        private static final long ACCESS_TOKEN_EXP = 15 * 60 * 1000; // 15 minutes in milliseconds
        private static final long REFRESH_TOKEN_EXP = 15 * 60 * 1000;// 15 minutes in milliseconds

        /**
         * Get Access JWT token
         */
        public String generateAccessToken(String userName) {
                Map<String, Object> claims = new HashMap<>();
                List<String> roles = Arrays.asList("USER");
                claims.put("roles", roles);
                claims.put("type", "access");
                String jti = UUID.randomUUID().toString();
                claims.put("jti", jti);
                String subject = userName;
                return Jwts.builder().setClaims(claims).setSubject(subject)
                                .setIssuedAt(new Date(System.currentTimeMillis()))
                                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXP))
                                .setId(jti)
                                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
        }

        private Key getSignKey() {
                byte[] keyBytes = Decoders.BASE64.decode(SECRET);
                return Keys.hmacShaKeyFor(keyBytes);
        }

        /**
         * Get Refresh JWT token
         */
        public String generateRefreshToken(String username) throws Exception {

                String subject = username;
                Map<String, Object> claims = new HashMap<>();
                claims.put("type", "refresh");
                // List<String> roles = Arrays.asList("USER", "ADMIN");
                // claims.put("roles", roles);
                String jti = UUID.randomUUID().toString();
                claims.put("jti", jti);
                String newrefreshToken = Jwts.builder()
                                .setClaims(claims)
                                .setSubject(subject)
                                .setIssuedAt(new Date(System.currentTimeMillis()))
                                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXP))
                                .setId(jti)
                                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                                .compact();
                return newrefreshToken;

        }

        /**
         * Validate JWT token
         */
        public boolean validateToken(String token) {
                try {
                        Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);
                        // System.out.println("Token validated");
                        return true;
                } catch (JwtException | IllegalArgumentException e) {
                        return false;
                }
        }

        public String extractSubject(String token) {
                Claims claims = Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token)
                                .getBody();

                return claims.getSubject();
        }

        public String extractUsername(String token) {
                return extractSubject(token);
        }

        /**
         * Get expiration date of a JWT token
         */
        public Date getExpiration(String token) {
                Claims claims = Jwts.parserBuilder()
                                .setSigningKey(getSignKey())
                                .build()
                                .parseClaimsJws(token)
                                .getBody();

                return claims.getExpiration();
        }

        public String extractTokenType(String token) {
                return Jwts.parserBuilder()
                                .setSigningKey(getSignKey())
                                .build()
                                .parseClaimsJws(token)
                                .getBody()
                                .get("type", String.class);
        }

        // Method to get the JTI (JWT ID) from a refresh token and access token
        public String getJti(String token) {
                try {
                        Claims claims = Jwts.parserBuilder()
                                        .setSigningKey(getSignKey())
                                        .build()
                                        .parseClaimsJws(token)
                                        .getBody();
                        // System.out.println("claims------" + claims.getId());
                        // System.out.println("claims------" + claims);
                        return claims.getId();

                } catch (SignatureException e) {
                        throw new JwtException("Invalid JWT signature: " + e.getMessage(), e);
                } catch (Exception e) {
                        throw new JwtException("Failed to parse JWT: " + e.getMessage(), e);
                }
        }

        public Claims validateClaimToken(String token) {
                // Use parserBuilder() and set the signing key
                return Jwts.parserBuilder()
                                .setSigningKey(getSignKey())
                                .build()
                                .parseClaimsJws(token) // parses and validates the token
                                .getBody(); // return claims
        }

        // Extract authorities (roles) from token
        public List<SimpleGrantedAuthority> getAuthoritiesFromToken(String token) {
                Claims claims = Jwts.parserBuilder()
                                .setSigningKey(getSignKey())
                                .build()
                                .parseClaimsJws(token)
                                .getBody();

                List<String> roles = (List<String>) claims.get("roles");
                // System.out.println("Role: " + roles);
                return roles.stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());
        }
}
/*
 * Add "ROLE_" prefix to each role hasrole
 * List<String> formattedRoles = roles.stream().map(role -> "ROLE_" +
 * role).collect(Collectors.toList());
 * claims.put("roles", formattedRoles);
 */