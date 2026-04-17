package com.cdac.statewide.gatewayservice.util;

import java.security.Key;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    public Claims validateToken(String token) {
        // Use parserBuilder() and set the signing key
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey()) // pass the key here
                .build()
                .parseClaimsJws(token) // parses and validates the token
                .getBody(); // return claims
    }

    private Key getSignKey() {
        // SECRET must be a Base64-encoded string
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractTokenType(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("type", String.class);
    }
}
