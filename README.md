# jsb_improvement

## 1. 프로젝트 소개

『점프 투 스프링부트』(JSB) 교재를 기반으로 질문·답변 게시판(SBB)을 완성하고,
장희성 강사의 강의(14163, 14168)에서 제시된 개선 방식을 검토·적용하는 학습 프로젝트입니다.

이 저장소는 기존 학습 프로젝트 `p-14163-1`의 초기 커밋을 로컬 clone으로 이어받고,
그 위에 미커밋 상태였던 학습 코드를 원형 그대로 이관하여 시작합니다.
빠른 구현보다 코드를 직접 이해하고 작은 단위로 기록하는 것을 우선합니다.

## 2. 프로젝트 목표

- 교재 본편 기능 완성 (2장 ~ 3-13)
- 3-14 추가 기능 9개 구현
- JPA·Thymeleaf·Spring Security 개념 학습
- 강사 구현과 교재 구현의 비교
- 테스트 기반 검증
- 작은 단위의 Git 이력 기록
- 개발 완료 후 커밋별 복습

## 3. 참고 자료

- 교재: https://wikidocs.net/book/7601
- 교재 원본 코드: https://github.com/pahkey/sbb3
- 14163 강의: https://www.slog.gg/p/14163/raw
- 14163 코드: https://github.com/jhs512/p-14163-2
- 14168 강의: https://www.slog.gg/p/14168/raw
- 14168 코드: https://github.com/jhs512/p-14168-2
- 도전과제 (3-14 추가 기능): https://wikidocs.net/162814

## 4. 기술 스택

현재 `build.gradle.kts`와 설정 파일에서 확인되는 내용만 기록합니다.

- Java 25 (Gradle toolchain)
- Spring Boot 3.5.0
- Gradle (Wrapper 9.8.2)
- Spring Web (Spring MVC)
- Spring Data JPA
- Thymeleaf, thymeleaf-layout-dialect
- H2 Database (runtime)
- Lombok
- Spring Boot DevTools
- JUnit 5, AssertJ (spring-boot-starter-test)

Spring Security, Validation, MySQL 드라이버는 아직 의존성에 없습니다.

## 5. 실행 환경

프로파일은 `application.yaml`에서 기본값 `dev`로 지정되어 있습니다.

| 프로파일 | 설정 파일 | 데이터베이스 | 비고 |
|---|---|---|---|
| dev | `application-dev.yaml` | H2 파일 DB (`./db_dev`, MySQL 호환 모드) | Thymeleaf 캐시 비활성, 템플릿을 파일 시스템에서 직접 읽음 |
| test | `application-test.yaml` | H2 인메모리 DB (`db_test`) | 테스트 클래스에서 `@ActiveProfiles("test")`로 사용 |

공통 설정: JPA `ddl-auto: update`, SQL 로그 출력 및 포맷, 바인딩 파라미터 TRACE 로깅.

H2 DB 파일(`*.mv.db`, `*.trace.db`, `*.lock.db`)은 `.gitignore`로 제외됩니다.
DB 계정 등 개인 설정값은 이 문서에 기록하지 않습니다.

**주의: 이 저장소에서의 빌드·실행·테스트는 아직 수행하지 않았습니다. 실행 가능 여부는 미검증입니다.**

## 6. 기능 구현 현황

"코드 존재"는 해당 코드가 저장소에 있다는 뜻이며, 정상 동작이 검증되었다는 뜻이 아닙니다.

### 코드 존재 (동작 미검증)

| 기능 | 위치 |
|---|---|
| 루트 URL → 질문 목록 리다이렉트 | `MainController` |
| 질문 목록 | `QuestionController.list`, `templates/question_list.html` |
| 질문 상세 | `QuestionController.detail`, `templates/question_detail.html` |
| 답변 작성 | `AnswerController.createAnswer`, `Question.addAnswer` |
| JPA 엔티티 및 연관관계 | `Question`(1) ↔ `Answer`(N), `@OneToMany(mappedBy)` / `@ManyToOne(fetch = LAZY)` |
| Repository | `QuestionRepository`(findBySubject, findBySubjectAndContent, findBySubjectLike), `AnswerRepository` |
| 서비스 | `QuestionService`(getList, getQuestion) |
| 404 예외 | `DataNotFoundException` (`@ResponseStatus(NOT_FOUND)`) |
| 샘플 데이터 생성 | `TestInitData` (ApplicationRunner, 질문 2건·답변 2건) |
| 테스트 코드 | `SbbApplicationTests`, `QuestionRepositoryTest`, `PostRepositoryTest` |

### 미구현

- 교재 2-13 이후 (웹 페이지 디자인, 부트스트랩, 내비게이션 바)
- 회원가입 및 로그인·로그아웃
- 질문·답변 수정·삭제
- 페이징, 추천, 검색, 마크다운, 앵커
- 폼 유효성 검증
- 3-14 추가 기능 (도전과제)

## 7. 교재 대비 개선 예정 사항

아래 항목은 모두 **계획**이며 아직 구현되지 않았습니다.

- 개발/테스트 DB 분리 검증
- JPA 연관관계 및 트랜잭션 경계 검토
- Thymeleaf 폼 처리 개선
- 입력값 검증 (Validation)
- Spring Security 도입
- 3-14 추가 기능 구현

## 8. 테스트 방법

테스트 코드는 `src/test/java/com/mysite/sbb` 아래에 있습니다.

| 클래스 | 대상 |
|---|---|
| `SbbApplicationTests` | `QuestionRepository.findAll` |
| `QuestionRepositoryTest` | 질문 조회·수정·삭제, 답변 생성·조회 (t1 ~ t11) |
| `PostRepositoryTest` | 위와 유사한 내용 (t1 ~ t12) |

테스트는 `test` 프로파일(인메모리 H2)을 사용하며, 리포지터리 테스트에는 `@Transactional`이 적용되어 각 테스트 후 롤백됩니다.

실행 명령(참고용, 아직 실행하지 않음):

```bash
./gradlew test
```

**현재 테스트 결과: 미검증.** 이 저장소에서 테스트를 실행한 적이 없습니다.

알려진 미검증 사항:

- `QuestionRepositoryTest`와 `PostRepositoryTest`가 내용이 중복되며, 답변 개수에 대한 기대값이 서로 다릅니다.
- `QuestionRepositoryTest.java`와 `DataNotFoundException.java`는 파일 위치와 `package` 선언이 일치하지 않습니다.
- 위 문제는 기준점 보존을 위해 원형 그대로 두었으며, 정리는 별도 작업으로 진행합니다.

## 작업내역

### 000-01 기존 학습 코드 보존, 교재 2-01~2-12 기준점

- 작업: 기존 학습 코드 19개 파일 이관 결과와 Git 제외 규칙을 기준 커밋으로 보존
- 이유: 이후 구현·개선 작업의 비교 기준 확보
- 교재 대응: 2-01~2-12
- 강사 참고: 14163 강의
- 검증: 원본과 이관 코드 SHA-256 일치, Git 상태 확인. 빌드·테스트는 미실행
- 알려진 문제: 중복 테스트, 일부 패키지 경로 불일치, 런타임 동작 미검증
- 학습 주제: Git 이력, Spring Boot 기본 구조, JPA 및 MVC
- Git 참조: 000-01
