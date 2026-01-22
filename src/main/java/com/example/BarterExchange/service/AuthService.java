package com.example.BarterExchange.service;

import com.example.BarterExchange.domain.Role;
import com.example.BarterExchange.domain.User;
import com.example.BarterExchange.repository.UserRepository;
import com.example.BarterExchange.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public String signup(String username, String rawPassword) {
        if (userRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(Role.USER);
        userRepository.save(user);
        return jwtTokenProvider.createAccessToken(user.getUsername(), user.getRole());
    }

    @Transactional(readOnly = true)
    public String login(String username, String rawPassword) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        return jwtTokenProvider.createAccessToken(user.getUsername(), user.getRole());
    }
}
