package com.example.BarterExchange.security;

import com.example.BarterExchange.domain.Role;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String secret;
    private final long accessExpireMinutes;

    public JwtTokenProvider(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.access-expire-min}") long accessExpireMinutes
    ) {
        this.secret = secret;
        this.accessExpireMinutes = accessExpireMinutes;
    }

    public String createAccessToken(String username, Role role) {
        long exp = Instant.now().plusSeconds(accessExpireMinutes * 60).getEpochSecond();
        return createToken(username, role, exp);
    }

    public JwtUser parseToken(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid token");
        }
        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        Map<String, Object> payload = readJson(payloadJson);
        String username = (String) payload.get("sub");
        String role = (String) payload.get("role");
        Number exp = (Number) payload.get("exp");
        if (username == null || role == null || exp == null) {
            throw new IllegalArgumentException("Invalid token payload");
        }
        long now = Instant.now().getEpochSecond();
        if (exp.longValue() < now) {
            throw new IllegalArgumentException("Token expired");
        }
        return new JwtUser(username, Role.valueOf(role));
    }

    public boolean validateSignature(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return false;
        }
        String data = parts[0] + "." + parts[1];
        String expected = sign(data);
        return constantTimeEquals(expected, parts[2]);
    }

    private String createToken(String username, Role role, long exp) {
        String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payloadJson = "{\"sub\":\"" + username + "\",\"role\":\"" + role.name() + "\",\"exp\":" + exp + "}";
        String header = Base64.getUrlEncoder().withoutPadding()
            .encodeToString(headerJson.getBytes(StandardCharsets.UTF_8));
        String payload = Base64.getUrlEncoder().withoutPadding()
            .encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
        String data = header + "." + payload;
        String signature = sign(data);
        return data + "." + signature;
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signature = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(signature);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to sign token", e);
        }
    }

    private Map<String, Object> readJson(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token payload", e);
        }
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }

    public record JwtUser(String username, Role role) {}
}
