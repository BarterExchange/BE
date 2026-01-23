package com.example.BarterExchange.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUserInfoResponse(
    Long id,
    @JsonProperty("kakao_account") KakaoAccount kakaoAccount,
    KakaoUserProperties properties
) {
    public String email() {
        return kakaoAccount == null ? null : kakaoAccount.email();
    }

    public record KakaoAccount(String email) {
    }

    public record KakaoUserProperties(String nickname) {
    }
}
