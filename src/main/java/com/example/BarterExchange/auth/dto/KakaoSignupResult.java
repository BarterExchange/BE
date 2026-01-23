package com.example.BarterExchange.auth.dto;

public record KakaoSignupResult(
        Long userId,
        boolean isNewUser
) {}
