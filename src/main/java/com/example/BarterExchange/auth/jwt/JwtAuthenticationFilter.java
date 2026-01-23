package com.example.BarterExchange.auth.jwt;

import com.example.BarterExchange.user.User;
import com.example.BarterExchange.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtProvider jwtProvider, UserRepository userRepository) {
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractAccessToken(request);

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // JwtProvider에서 검증 시 에러가 나면 터미널에 원인이 찍힐 것입니다.
            if (jwtProvider.isValid(token)) {
                Long userId = jwtProvider.getUserId(token);
                Optional<User> userOptional = userRepository.findById(userId);

                if (userOptional.isPresent()) {
                    // 유저가 존재하면 인증 객체를 생성하여 컨텍스트에 저장
                    Authentication authentication =
                            new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("✅ [AUTH SUCCESS] User ID: " + userId + "님 인증 성공");
                } else {
                    System.out.println("❌ [AUTH FAIL] DB에 해당 유저(ID: " + userId + ")가 없습니다.");
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractAccessToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        for (Cookie cookie : cookies) {
            if ("access_token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}