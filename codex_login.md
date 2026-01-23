현재 Spring Boot 기반 백엔드에서 카카오 OAuth 회원가입 플로우를 구현 중이다.

[현재 구조]
- 카카오 OAuth 인증 후 User 엔티티를 DB에 저장
- 신규 유저는 SignupStatus.PENDING 상태로 생성됨
- POST /api/signup/complete 요청 시:
    - userId(PK) 기준으로 User 조회
    - nickname 설정
    - SignupStatus를 ACTIVE로 변경
- 회원가입 완료 이후 현재는 JWT를 발급하지 않음

[요구사항]
1. 회원가입 완료(SignupStatus가 ACTIVE로 변경되는 시점)에 JWT를 발급하여 자동 로그인 처리하고 싶다.
2. JWT는 Access Token 방식만 사용한다. (Refresh Token은 이번 범위에서 제외)
3. JWT는 HttpOnly 쿠키로 클라이언트에 전달한다.
4. JWT Payload에는 최소한 다음 정보가 포함되어야 한다.
    - userId (DB PK)
5. Spring Security 기반으로 인증을 구성한다.
6. 회원가입 완료 API의 응답은 다음 방식으로 설계한다.
    - 200 OK + Set-Cookie(JWT)
7. 기존 카카오 OAuth 인증 흐름은 변경하지 않는다.
8. 회원가입 완료 이전(PENDING 상태)에는 JWT가 절대 발급되면 안 된다.

[중요 제약 사항 - 반드시 지킬 것]
- build.gradle 파일은 수정하지 않는다.
- Dockerfile은 수정하지 않는다.
- docker-compose.yml은 수정하지 않는다.
- CI/CD 관련 설정은 수정하지 않는다.
- GitHub 이슈 템플릿 등 프로젝트 관리용 파일은 수정하지 않는다.
- 의존성 추가가 필요하다면, "이미 존재한다고 가정"하고 코드만 제시한다.

[요청 사항]
아래 항목을 포함하여 전체 흐름이 자연스럽게 연결되도록 설계 및 코드 예시를 제공해달라.

1. JWT 발급을 담당하는 JwtProvider (또는 TokenProvider) 설계
    - 토큰 생성
    - 토큰 검증
    - Payload 파싱
2. 회원가입 완료(SignupCompleteService 또는 SignupCompleteController)에서
    - SignupStatus ACTIVE 처리 이후
    - JWT 생성
    - HttpOnly 쿠키로 응답에 포함시키는 로직 추가
3. Spring Security 설정(SecurityConfig)에서
    - JWT 인증 필터 적용 방법
    - 인증이 필요한 API / 필요 없는 API 구분
4. 전체 인증 흐름을 이해할 수 있도록 간단한 시퀀스 설명

[설계 의도]
- 회원가입과 로그인은 개념적으로 분리된 구조를 유지한다.
- 자동 로그인은 "회원가입 완료 시 JWT 발급"으로만 구현한다.
- 이후 일반 로그인 API 확장이 가능한 구조여야 한다.
