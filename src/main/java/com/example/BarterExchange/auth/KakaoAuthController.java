package com.example.BarterExchange.auth;

import com.example.BarterExchange.auth.dto.KakaoAuthRequest;
import com.example.BarterExchange.auth.dto.KakaoSignupResult;
import com.example.BarterExchange.user.UserRepository; // import 추가
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // import 추가
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;
    private final UserRepository userRepository; // 추가된 필드

    // ★ 생성자에서 userRepository도 함께 주입받아야 합니다.
    public KakaoAuthController(KakaoAuthService kakaoAuthService, UserRepository userRepository) {
        this.kakaoAuthService = kakaoAuthService;
        this.userRepository = userRepository;
    }

    @PostMapping("/kakao")
    public ResponseEntity<KakaoSignupResult> kakaoSignup(
            @Valid @RequestBody KakaoAuthRequest request
    ) {
        KakaoSignupResult result = kakaoAuthService.handleKakaoSignup(request.code());
        return ResponseEntity.ok(result);
    }

    // 내 정보 조회 엔드포인트
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("인증 정보가 없습니다.");
        }

        // JwtAuthenticationFilter에서 저장한 userId(Long)를 꺼냅니다.
        Long userId = (Long) authentication.getPrincipal();

        return userRepository.findById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).build());
    }
}