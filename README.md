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

### 001-01 기준선 복구, 중복 테스트 정리

- 작업: `PostRepositoryTest` 삭제. 고유 검증이던 t12(2번 질문 첫 답변 id=1)와 t8의 `id > 0` 단언을 `QuestionRepositoryTest`로 이동
- 이유: 두 클래스가 같은 리포지터리를 검증하면서 답변 개수 기대값(1 vs 2)이 달라 t11이 실패했음. 샘플 데이터(`TestInitData`)와 강사 최종 코드 모두 답변 2개이므로 2가 올바른 값
- 교재 대응: 2-05 리포지터리 테스트
- 강사 코드와의 차이: 강사 저장소(p-14163-2 태그 039)는 `QuestionRepositoryTest` 하나만 유지. 동일한 구성으로 정리
- 수정 파일: `src/test/java/com/mysite/sbb/QuestionRepositoryTest.java`, `src/test/java/com/mysite/sbb/question/PostRepositoryTest.java`(삭제)
- 검증: `./gradlew test` 실행, 13개 테스트 전부 통과(24개 → 13개, 중복 11개 제거·고유 검증 1개 보존)
- 학습 개념: 테스트 간 독립성(`@Transactional` 롤백), 샘플 데이터와 기대값의 일치, IDENTITY 전략의 즉시 INSERT
- 제한 사항: 테스트 클래스 파일 위치와 package 불일치는 다음 커밋에서 정리

### 001-02 기준선 복구, 파일 위치와 package 선언 일치

- 작업: `QuestionRepositoryTest.java`를 `question/` 폴더로, `DataNotFoundException.java`를 `com/mysite/sbb/` 루트로 이동(`git mv`). 코드 내용은 변경하지 않음
- 이유: 두 파일의 물리적 경로가 `package` 선언과 달라 IDE 경고가 발생하고 탐색이 혼란스러웠음. Gradle 컴파일에는 영향이 없었으나 교재·강사 코드와 같은 구조로 정리
- 교재 대응: 2-06 도메인별로 분류하기
- 강사 코드와의 차이: 강사 저장소와 동일한 위치(`DataNotFoundException`은 루트 패키지, 테스트는 `question` 패키지)
- 수정 파일: 이동 2건
- 검증: `./gradlew test` 13개 통과. `QuestionService`의 `import com.mysite.sbb.DataNotFoundException` 참조 유지 확인
- 학습 개념: Java 패키지와 디렉터리 구조의 관계, `git mv`로 이력 보존
- 제한 사항: 없음

### 002-01 화면 기반, 부트스트랩·공통 레이아웃·내비게이션 바

- 작업: `layout.html`(공통 레이아웃)·`navbar.html`·`static/style.css` 추가. `question_list.html`과 `question_detail.html`을 `layout:decorate`로 레이아웃을 상속하도록 재작성하고 부트스트랩 테이블·카드 적용
- 이유: 교재 2-13~2-15, 3-01의 화면 기반을 한 번에 마련해 이후 폼·페이징·로그인 화면이 같은 틀을 쓰게 함
- 교재 대응: 2-13 웹 페이지 디자인, 2-14 부트스트랩, 2-15 템플릿 상속, 3-01 내비게이션 바
- 강사 코드와의 차이: 교재는 `bootstrap.min.css/js`를 저장소에 복사하지만 강사(14163 39강)처럼 cdnjs 링크를 사용해 바이너리를 저장소에 넣지 않음. 강사 14168 45강의 `layout:title-pattern`으로 페이지별 `<title>` 합성. 답변 목록은 교재 순서(작성순)로 표시하고 기존의 `.reversed`는 제거
- 수정 파일: `templates/layout.html`, `templates/navbar.html`, `static/style.css`, `templates/question_list.html`, `templates/question_detail.html`, `test/.../QuestionControllerTest.java`(신규)
- 검증: `./gradlew test` 17개 통과(MockMvc 컨트롤러 테스트 4개 추가). `bootRun` 후 브라우저에서 `/question/list`, `/question/detail/2` 렌더링 확인(제목 `질문 목록 | SBB`, 카드·테이블 표시)
- 학습 개념: Thymeleaf Layout Dialect(`layout:decorate`, `layout:fragment`), `th:replace` 프래그먼트, `#temporals` 날짜 포맷, MockMvc로 뷰 렌더링 검증
- 제한 사항: 내비게이션 바의 로그인·회원가입 링크는 Security 도입 단계에서 추가

### 003-01 질문 등록, 폼·입력값 검증

- 작업: `spring-boot-starter-validation` 추가. `QuestionForm`(record, `@NotBlank`/`@Size`)과 `QuestionService.create`, `QuestionController`의 `/question/create` GET·POST 추가. `question_form.html`과 공통 오류 프래그먼트 `form_errors.html` 작성, 목록 화면에 "질문 등록하기" 버튼 추가
- 이유: 교재 2-16 질문 등록 기능. 입력값 검증 실패 시 예외가 아니라 폼을 다시 보여주고 입력값을 유지하기 위해 `@Valid` + `BindingResult` 사용
- 교재 대응: 2-16 질문 등록 및 폼
- 강사 코드와의 차이: 폼 DTO를 record로 작성(14168 40강). 오류 메시지를 "순번-필드명-메시지" 형식으로 적고 `form_errors.html`에서 정렬·필드명 추출·첫 오류 필드 자동 포커스(14168 23~25강). `th:action` 값을 비워 현재 URL로 전송(14168 5강). 모델 이름은 `form`으로 통일(14168 18강)
- 수정 파일: `build.gradle.kts`, `question/QuestionForm.java`(신규), `question/QuestionService.java`, `question/QuestionController.java`, `templates/question_form.html`(신규), `templates/form_errors.html`(신규), `templates/question_list.html`, `test/.../QuestionControllerTest.java`
- 검증: `./gradlew test` 20개 통과(폼 표시·정상 등록·빈 제목 거부 테스트 3개 추가). 브라우저에서 빈 폼 제출 시 오류 메시지 2개 표시와 등록 거부 확인
- 학습 개념: Bean Validation(`@NotBlank`, `@Size`), `@ModelAttribute`와 `th:object`/`th:field`, `BindingResult`의 역할, `#fields.allErrors()`
- 제한 사항: 의존성을 추가한 뒤에는 실행 중인 `bootRun`을 재시작해야 검증이 동작함(DevTools 재시작은 새 의존성을 반영하지 못함). 작성자 정보는 회원 기능 이후 추가

### 003-02 답변 등록, 입력값 검증

- 작업: `AnswerForm`(record) 추가. `AnswerController.createAnswer`가 `@Valid`·`BindingResult`로 검증하고 실패 시 질문 상세 화면을 다시 렌더링. `QuestionController.detail`에 `answerForm` 모델 속성 추가, `question_detail.html` 답변 폼을 `th:object`/`th:field`와 오류 프래그먼트로 변경
- 이유: 교재 2-16의 답변 폼 검증. 빈 답변이 저장되지 않도록 서버 측에서 거부
- 교재 대응: 2-16 질문 등록 및 폼(답변 부분)
- 강사 코드와의 차이: 교재는 `AnswerService.create`로 저장하지만, 기존 코드의 `question.addAnswer()` + 컨트롤러 `@Transactional`(cascade PERSIST) 방식을 유지(14163 37강). 폼은 record
- 수정 파일: `answer/AnswerForm.java`(신규), `answer/AnswerController.java`, `question/QuestionController.java`, `templates/question_detail.html`, `test/.../AnswerControllerTest.java`(신규)
- 검증: `./gradlew test` 23개 통과(답변 등록·빈 답변 거부·없는 질문 404 테스트 3개 추가)
- 학습 개념: 한 화면에 두 폼 객체가 있을 때 모델 이름 구분(`form` vs `answerForm`), 검증 실패 시 원래 화면 재렌더링에 필요한 모델 복구
- 제한 사항: 브라우저 검증은 다음 커밋의 화면 확인과 함께 수행

### 004-01 질문 목록 페이징

- 작업: `QuestionRepository.findAll(Pageable)` 선언, `QuestionService.getList(int page)`(10건, 작성일시·id 내림차순), `QuestionController.list`의 `page` 파라미터, `question_list.html`에 부트스트랩 페이지 네비게이션 추가. `TestInitData`에 dev 프로파일 전용 샘플 질문 300건 생성
- 이유: 교재 3-02 페이징. 글이 많아져도 목록이 한 화면에 들어가도록 함
- 교재 대응: 3-02 페이징 기능 추가하기
- 강사 코드와의 차이: 교재는 테스트 코드로 300건을 넣지만, 테스트가 "질문 2건"을 전제로 하므로 `Environment.matchesProfiles("dev")`로 개발 실행 시에만 생성. 작성일시가 같은 글의 순서가 흔들리지 않도록 `id` 2차 정렬 추가(교재에는 없음)
- 수정 파일: `question/QuestionRepository.java`, `question/QuestionService.java`, `question/QuestionController.java`, `templates/question_list.html`, `TestInitData.java`, `test/.../QuestionControllerTest.java`
- 검증: `./gradlew test` 24개 통과. dev DB 초기화 후 `bootRun`, 브라우저에서 `/question/list?page=1`이 10건과 페이지 링크(이전·0~6·다음)를 표시함을 확인
- 학습 개념: `Pageable`/`PageRequest`/`Page`, `Sort` 다중 정렬, `#numbers.sequence`, 프로파일별 초기 데이터 분기
- 제한 사항: 페이지 링크가 `?page=N` 단순 링크라서 검색어와 함께 쓰려면 3-13에서 폼 전송 방식으로 바꿔야 함

### 004-02 게시물 번호와 답변 개수 표시

- 작업: 목록의 번호 열을 DB id 대신 `전체 건수 - (페이지 번호 × 페이지 크기) - 행 인덱스`로 계산해 최신 글이 가장 큰 번호를 갖게 함. 답변이 있는 질문 제목 옆에 답변 개수 배지 표시
- 이유: 교재 3-03, 3-04. 페이징 이후 번호가 페이지마다 1부터 시작하지 않도록 함
- 교재 대응: 3-03 게시물에 번호 지정하기, 3-04 답변 개수 표시하기
- 강사 코드와의 차이: 없음(교재와 동일 수식). 답변 개수는 `#lists.size(question.answers)`로 행마다 지연 로딩이 일어나므로, 글이 많아지면 N+1 조회가 생긴다는 점을 학습 포인트로 기록
- 수정 파일: `templates/question_list.html`, `test/.../QuestionControllerTest.java`
- 검증: `./gradlew test` 24개 통과. 브라우저에서 마지막 페이지(`?page=30`)의 번호가 2, 1이고 2번 질문 옆에 답변 개수 2가 표시됨을 확인
- 학습 개념: Thymeleaf 반복 상태 변수(`loop.index`), `Page`의 `totalElements`/`number`/`size`, 지연 로딩과 N+1
- 제한 사항: 답변 개수 조회의 N+1 최적화는 미션 2 이후 과제로 남김

### 005-01 Spring Security 도입

- 작업: `spring-boot-starter-security`, `thymeleaf-extras-springsecurity6`, `spring-security-test` 의존성 추가. `SecurityConfig`에서 모든 URL 허용, CSRF 유지(H2 콘솔만 예외), 같은 출처 프레임 허용, `BCryptPasswordEncoder` 빈 등록, `@EnableMethodSecurity`. dev 프로파일에 H2 콘솔 활성화. 기존 MockMvc POST 테스트에 `csrf()` 적용
- 이유: 교재 3-05. 회원가입·로그인 구현의 기반이며, CSRF 보호를 끄지 않고 폼 전송이 동작하도록 함
- 교재 대응: 3-05 스프링 시큐리티란?
- 강사 코드와의 차이: 강사(14168 46강)도 전체 허용 + 정적 자원·H2 콘솔 예외로 시작. `AntPathRequestMatcher` 대신 Boot 3.5의 문자열 `requestMatchers("/**")` 사용. 메서드 보안(`@PreAuthorize`)을 처음부터 활성화
- 수정 파일: `build.gradle.kts`, `SecurityConfig.java`(신규), `application-dev.yaml`, `test/.../QuestionControllerTest.java`, `test/.../AnswerControllerTest.java`
- 검증: `./gradlew test` 24개 통과(CSRF 토큰 없는 POST는 403이 되므로 테스트에 `with(csrf())` 추가)
- 학습 개념: `SecurityFilterChain`, CSRF 토큰과 `th:action`, `X-Frame-Options`, `PasswordEncoder`
- 제한 사항: 로그인 페이지·회원 엔티티는 다음 커밋. 새 의존성이므로 실행 중인 서버는 재시작 필요

### 005-02 회원가입 기능

- 작업: `user` 패키지에 `SiteUser` 엔티티(username·email 유니크), `UserRole`, `UserRepository`, `UserService.create`(BCrypt 해시 저장), `UserCreateForm`(record), `UserController` `/user/signup` GET·POST, `signup_form.html` 추가. 내비게이션 바에 회원가입 링크
- 이유: 교재 3-06 회원가입. 비밀번호 일치·중복 ID·이메일 형식을 검증하고 비밀번호를 평문으로 저장하지 않음
- 교재 대응: 3-06 회원 가입 기능 구현하기
- 강사 코드와의 차이: 폼은 record. 중복 ID는 저장 전에 `findByUsername`으로 먼저 확인해 필드 오류로 표시하고(교재는 예외만 잡음), DB 유니크 제약 위반(`DataIntegrityViolationException`)은 교재처럼 보조 처리. 엔티티 id는 프로젝트의 다른 엔티티와 같은 `int`
- 수정 파일: `user/SiteUser.java`, `user/UserRole.java`, `user/UserRepository.java`, `user/UserService.java`, `user/UserCreateForm.java`, `user/UserController.java`, `templates/signup_form.html`(이상 신규), `templates/navbar.html`, `test/.../UserControllerTest.java`(신규)
- 검증: `./gradlew test` 29개 통과(폼 표시·가입 후 BCrypt 검증·비밀번호 불일치·중복 ID·형식 오류 테스트 5개 추가)
- 학습 개념: `PasswordEncoder.encode/matches`, `BindingResult.rejectValue/reject`로 수동 오류 추가, `@Column(unique = true)`
- 제한 사항: 로그인은 다음 커밋. 브라우저 가입 확인은 로그인 구현 후 한 번에 수행
