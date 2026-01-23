package com.example.BarterExchange.signup;

import com.example.BarterExchange.auth.jwt.JwtProvider;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/signup")
public class SignupCompleteController {

    private final SignupCompleteService signupCompleteService;
    private final JwtProvider jwtProvider;

    public SignupCompleteController(SignupCompleteService signupCompleteService, JwtProvider jwtProvider) {
        this.signupCompleteService = signupCompleteService;
        this.jwtProvider = jwtProvider;
    }

    @PostMapping("/complete")
    public ResponseEntity<Void> completeSignup(@Valid @RequestBody SignupCompleteRequest request) {
        String accessToken = signupCompleteService.complete(request);

        ResponseCookie cookie = ResponseCookie.from(JwtProvider.ACCESS_TOKEN_COOKIE, accessToken)
                .httpOnly(true)
                .path("/")
                .maxAge(jwtProvider.getAccessTokenDuration())
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }
}
