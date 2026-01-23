카카오 OAuth2 Authorization Code Flow 기반
Backend(BE) 회원가입(New User) 로직만 구현해줘.

[중요 – 구현 범위 제한]

이번 작업의 목표는 "카카오 신규 회원 회원가입 처리"이다.
로그인(기존 회원) 로직과 서비스 JWT 발급은 구현하지 않는다.

이미 Frontend(FE)에서는 다음 흐름이 구현되어 있다:
- 카카오 인증 후 redirect_uri에서 인가 코드(code) 수신
- code를 POST /api/auth/kakao 로 전달

[기술 스택]

- Java 21
- Spring Boot
- Spring Data JPA
- MySQL
- (선택) RestTemplate 또는 WebClient

[중요 – 기존 설정 유지]

다음 파일 및 설정은 이미 운영을 고려해 작성되어 있으므로
절대 수정하지 말고 유지해야 한다:

- Dockerfile
- docker-compose.yml
- application.properties
- CI/CD 관련 파일

[회원가입 처리 흐름 (BE)]

1. POST /api/auth/kakao 에서 인가 코드(code) 수신
2. code + client_id + client_secret으로
   카카오 OAuth 서버에 토큰 발급 요청
3. 발급받은 access_token으로 카카오 사용자 정보 조회
4. 카카오 사용자 고유 식별자(kakaoId)를 기준으로
   우리 서비스 회원 존재 여부 확인
5. 회원이 존재하지 않으면:
    - 신규 User 엔티티 생성
    - 최소 필드만 저장
      (예: kakaoId, email, nickname, signupStatus)
6. FE에 다음 형태로 응답:
```json
{
  "isNewUser": true
}
