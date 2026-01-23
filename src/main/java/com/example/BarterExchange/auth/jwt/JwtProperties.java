package com.example.BarterExchange.auth.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secret;
    private long accessExpireMin;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getAccessExpireMin() {
        return accessExpireMin;
    }

    public void setAccessExpireMin(long accessExpireMin) {
        this.accessExpireMin = accessExpireMin;
    }
}
