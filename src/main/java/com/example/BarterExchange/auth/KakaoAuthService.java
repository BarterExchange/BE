package com.example.BarterExchange.auth;

import com.example.BarterExchange.auth.dto.KakaoAuthResponse;
import com.example.BarterExchange.auth.dto.KakaoSignupResult;
import com.example.BarterExchange.auth.dto.KakaoTokenResponse;
import com.example.BarterExchange.auth.dto.KakaoUserInfoResponse;
import com.example.BarterExchange.user.SignupStatus;
import com.example.BarterExchange.user.User;
import com.example.BarterExchange.user.UserRepository;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.*;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoAuthService {

    private final RestTemplate restTemplate;
    private final KakaoOAuthProperties properties;
    private final UserRepository userRepository;

    public KakaoAuthService(
            RestTemplate restTemplate,
            KakaoOAuthProperties properties,
            UserRepository userRepository
    ) {
        this.restTemplate = restTemplate;
        this.properties = properties;
        this.userRepository = userRepository;
    }

    public KakaoSignupResult handleKakaoSignup(String code) {
        String accessToken = requestAccessToken(code);
        KakaoUserInfoResponse userInfo = requestUserInfo(accessToken);

        Long kakaoId = userInfo.id();

        User user = userRepository.findByKakaoId(kakaoId)
                .orElseGet(() -> {
                    User newUser = new User(
                            kakaoId,
                            userInfo.properties() != null
                                    ? userInfo.properties().nickname()
                                    : null,
                            SignupStatus.PENDING
                    );
                    return userRepository.save(newUser);
                });

        boolean isNewUser = user.getSignupStatus() == SignupStatus.PENDING;

        return new KakaoSignupResult(user.getId(), isNewUser);
    }

    private String requestAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", properties.clientId());
        body.add("client_secret", properties.clientSecret());
        body.add("redirect_uri", "http://localhost:3001/auth/kakao/callback");
        body.add("code", code);

        HttpEntity<?> request = new HttpEntity<>(body, headers);

        KakaoTokenResponse response = restTemplate.postForObject(
                properties.tokenUri(),
                request,
                KakaoTokenResponse.class
        );

        return response.accessToken();
    }

    private KakaoUserInfoResponse requestUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        ResponseEntity<KakaoUserInfoResponse> response =
                restTemplate.exchange(
                        properties.userInfoUri(),
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        KakaoUserInfoResponse.class
                );

        return response.getBody();
    }
}
