package com.example.BarterExchange.signup;

import com.example.BarterExchange.auth.jwt.JwtProvider;
import com.example.BarterExchange.user.User;
import com.example.BarterExchange.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignupCompleteService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public SignupCompleteService(UserRepository userRepository, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
    }

    @Transactional
    public String complete(SignupCompleteRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        user.completeSignup(request.getNickname());
        return jwtProvider.createAccessToken(user.getId());
    }
}
