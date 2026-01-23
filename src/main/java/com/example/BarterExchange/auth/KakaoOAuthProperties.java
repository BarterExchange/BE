package com.example.BarterExchange.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kakao")
public record KakaoOAuthProperties(
    String clientId,
    String clientSecret,
    String redirectUri,
    String tokenUri,
    String userInfoUri
) {
}
