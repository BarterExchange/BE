package com.example.BarterExchange.controller;

import com.example.BarterExchange.controller.dto.AuthLoginRequest;
import com.example.BarterExchange.controller.dto.AuthResponse;
import com.example.BarterExchange.controller.dto.AuthSignupRequest;
import com.example.BarterExchange.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public AuthResponse signup(@Valid @RequestBody AuthSignupRequest request) {
        String token = authService.signup(request.getUsername(), request.getPassword());
        return new AuthResponse(token);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthLoginRequest request) {
        String token = authService.login(request.getUsername(), request.getPassword());
        return new AuthResponse(token);
    }
}
