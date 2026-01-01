프로젝트 개요
  1차 백엔드 프로젝트
  Spring Boot 기반으로 구현한 RESTful 백엔드입니다.
  프론트엔드와의 명확한 인터페이스, 계층 분리, 전역 예외 처리 및 트랜잭션 관리를 중점으로 설계했습니다.

기술 스택
- 언어 / 플랫폼: Java 17(JDK17), Spring Boot(3.5.9)
- ORM / SQL: MyBatis(3.0.3) (매퍼 파일로 쿼리 분리)
- 개발 도구: IntelliJ IDEA
- 아키텍처: MVC(Model 2) 기반 3계층 구조 (Controller → Service → Repository)

주요 설계 및 기능
- 응답 표준화: 모든 API 응답은 { message, data } 형태로 통일되어 프론트에서 파싱하기 쉽습니다.
- 트랜잭션 경계: 핵심 비즈니스 로직은 Service 계층에서 @Transactional로 관리하여 책임을 분리했습니다.
- 예외 처리: GlobalExceptionHandler를 통해 전역 예외를 일관되게 처리합니다.
- SQL 관리: MyBatis 매퍼 파일로 SQL을 분리하여 유지보수성과 가독성을 높였습니다.
- 역할 분리: Controller는 요청/응답 처리와 DTO 변환, 인증/권한 체크 등 주변 로직만 담당합니다.
- 테스트: JUnit을 사용해 단위 테스트와 서비스 계층의 비즈니스 로직을 검증합니다.
  주요 서비스 메서드에 대한 단위 테스트와 예외/트랜잭션 경계에 대한 통합 테스트를 포함합니다.

작성자 : 전승훈
