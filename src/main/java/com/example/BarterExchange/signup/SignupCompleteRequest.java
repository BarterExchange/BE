package com.example.BarterExchange.signup;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SignupCompleteRequest {

    @NotNull
    private Long userId;   // 🔥 kakaoId → userId

    @NotBlank
    private String nickname;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
