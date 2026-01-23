카카오 OAuth2 Authorization Code Flow 기반
프론트엔드(FE) 회원가입 로직만 구현해줘.

[중요 – 목표 범위]

이번 작업의 목표는 "회원가입(New User) 플로우" 구현이다.
로그인(기존 회원) 로직은 구현하지 않는다.

Backend(BE) 로직은 구현 대상이 아니다.
FE 코드만 작성한다.

[프로젝트 전제]

이미 Backend에는 다음 API가 존재한다고 가정한다:

- POST /api/auth/kakao
  - FE가 인가 코드(code)를 전달
  - BE가 카카오 인증 처리
  - 신규 회원이면 isNewUser=true 응답

[FE 기술 스택]

- Framework: Next.js
- Language: TypeScript
- HTTP Client: Axios
- State Management: Zustand
- Styling: Tailwind CSS

[회원가입 인증 흐름 (FE 기준)]

1. 사용자가 "카카오로 회원가입" 버튼 클릭
2. FE는 카카오 OAuth 인증 페이지로 redirect
3. 사용자가 카카오 계정으로 로그인 및 인가 동의
4. 카카오는 사전에 등록된 redirect_uri(FE)로
   인가 코드(code)를 전달
5. FE는 redirect_uri 페이지에서 code를 추출
6. FE는 code를 BE API(/api/auth/kakao)로 전달
7. BE 응답에서 isNewUser=true 확인
8. FE는 회원가입 전용 페이지로 이동
   (예: 추가 정보 입력 화면)

[역할 분리 규칙 – 매우 중요]

- FE는 절대 다음 작업을 하지 않는다:
  - client_secret 사용
  - 카카오 access_token / refresh_token 발급
  - 카카오 사용자 정보 조회
  - 로그인(기존 회원) 처리

- FE는 오직 다음만 수행한다:
  - 카카오 인증 페이지 이동
  - redirect_uri에서 code 수신
  - code를 BE로 전달
  - 신규 회원 여부 판단 후 회원가입 UI로 이동

[환경 변수 규칙]

- FE에서는 NEXT_PUBLIC_KAKAO_CLIENT_ID만 사용
- client_secret은 FE 코드에 존재하면 안 됨

[구현 범위]

1. "카카오로 회원가입" 버튼 컴포넌트
   - Tailwind CSS 사용
2. 카카오 OAuth 인증 URL 생성 로직
3. redirect_uri 페이지 구현 (Next.js)
   - URL에서 code 파라미터 추출
4. Axios를 이용해 BE로 code 전달
5. Zustand를 이용한 회원가입 상태 관리
   - isNewUser 상태 저장
6. 신규 회원일 경우 이동할 회원가입 페이지 예시

[BE 응답 예시 – 회원가입 대상]

```json
{
  "isNewUser": true
}
