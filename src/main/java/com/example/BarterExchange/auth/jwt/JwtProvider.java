package com.example.BarterExchange.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    public static final String ACCESS_TOKEN_COOKIE = "access_token";
    private final SecretKey key;
    private final long accessExpireMin;

    public JwtProvider(JwtProperties properties) {
        this.key = Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
        this.accessExpireMin = properties.getAccessExpireMin();
    }

    public String createAccessToken(Long userId) {
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + Duration.ofMinutes(accessExpireMin).toMillis());
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(expiresAt)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    // 빌드 에러 해결을 위해 추가된 메서드
    public Duration getAccessTokenDuration() {
        return Duration.ofMinutes(accessExpireMin);
    }

    public boolean isValid(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception ex) {
            System.out.println("❌ [JWT VALID ERROR] 원인: " + ex.getMessage());
            return false;
        }
    }

    public Long getUserId(String token) {
        Claims claims = parseToken(token).getPayload();
        return Long.valueOf(claims.getSubject());
    }

    private Jws<Claims> parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
    }
}