[요구사항 시작]

이미 존재하는 BarterExchange 백엔드 프로젝트가 있다.

[현재 상태]

- GitHub Actions 워크플로우 설정 완료
- Issue Template 존재
- Dockerfile 존재
- docker-compose.yml 존재
- application.properties 설정 완료

위 설정 파일들은 이미 운영을 고려해 구성되어 있으므로
절대 수정하지 말고 유지해야 한다.

[요청]
기존 프로젝트 구조를 유지한 채,
아래 요구사항을 기반으로 도메인 로직과 REST API를 구현해줘.

가능한 경우 실제 Java 코드 형태로 작성하되,
파일 단위로 구분해서 제시해줘.

[기술 스택]

- Java 21
- Spring Boot
- Spring Security (JWT)
- Spring Data JPA
- MySQL
- Redis (동시성 제어)
- Gradle

[구현 범위]

1. 도메인 엔티티 추가
   - User
   - Item
   - Offer
   - Exchange
   - ChatRoom
2. Enum 정의
   - ItemStatus
   - OfferStatus
3. 핵심 비즈니스 로직
   - Offer 생성
   - Offer 수락 (트랜잭션 + 동시성 보장)
   - Exchange 완료 처리
4. Controller API
   - 요구사항에 명시된 엔드포인트 그대로 사용
5. 트랜잭션 경계와 동시성 제어 지점에 주석으로 명확히 표시

[구현 규칙]

- 기존 패키지 구조가 있다면 이를 존중할 것
- 없다면 controller / service / domain / repository 기준으로 구성
- 동시성 제어 방식은 하나를 선택하되 선택 이유를 주석으로 설명

[주의사항]

- Dockerfile, docker-compose.yml, application.properties 수정 금지
- CI/CD 관련 파일 수정 금지

# 📌 중고 물물교환 서비스

## 최소 필수 기능 요구조건 (Core + Role 분리)

## 사용자 역할
Role
- USER
- ADMIN

## ItemStatus
- ACTIVE
- RESERVED
- EXCHANGED

## OfferStatus
- PENDING
- ACCEPTED
- REJECTED

## 핵심 규칙 요약
- Offer 수락은 트랜잭션으로 처리
- 하나의 Item에 대해 ACCEPTED Offer는 하나만 가능
- 상태 전이:
  ACTIVE → RESERVED → EXCHANGED
  RESERVED → ACTIVE (불가)
  EXCHANGED → ACTIVE (불가)

## 주요 API
- POST /api/auth/signup
- POST /api/auth/login
- POST /api/items
- POST /api/offers
- POST /api/offers/{offerId}/accept
- POST /api/offers/{offerId}/reject
- POST /api/exchanges/{exchangeId}/confirm/owner
- POST /api/exchanges/{exchangeId}/confirm/proposer

[요구사항 끝]
