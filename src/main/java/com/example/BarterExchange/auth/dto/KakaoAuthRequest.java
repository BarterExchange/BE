package com.example.BarterExchange.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record KakaoAuthRequest(@NotBlank String code) {
}
