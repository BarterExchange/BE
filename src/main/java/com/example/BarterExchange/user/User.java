package com.example.BarterExchange.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long kakaoId;


    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SignupStatus signupStatus;

    protected User() {
    }

    public User(Long kakaoId, String nickname, SignupStatus signupStatus) {
        this.kakaoId = kakaoId;
        this.nickname = nickname;
        this.signupStatus = signupStatus;
    }

    public Long getId() {
        return id;
    }

    public Long getKakaoId() {
        return kakaoId;
    }


    public String getNickname() {
        return nickname;
    }

    public SignupStatus getSignupStatus() {
        return signupStatus;
    }

    public void completeSignup(String nickname) {
        this.nickname = nickname;
        this.signupStatus = SignupStatus.ACTIVE;
    }
}
