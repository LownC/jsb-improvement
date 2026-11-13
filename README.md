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
- JUnit 5, AssertJ (spring-boot-starter-test), spring-security-test
- Spring Security, thymeleaf-extras-springsecurity6 (005-01)
- Bean Validation (spring-boot-starter-validation) (003-01)
- commonmark (마크다운 → HTML) (011-01)

MySQL 드라이버는 사용하지 않습니다(H2 파일 DB로 개발).

## 5. 실행 환경

프로파일은 `application.yaml`에서 기본값 `dev`로 지정되어 있습니다.

| 프로파일 | 설정 파일 | 데이터베이스 | 비고 |
|---|---|---|---|
| dev | `application-dev.yaml` | H2 파일 DB (`./db_dev`, MySQL 호환 모드) | Thymeleaf 캐시 비활성, 템플릿을 파일 시스템에서 직접 읽음 |
| test | `application-test.yaml` | H2 인메모리 DB (`db_test`) | 테스트 클래스에서 `@ActiveProfiles("test")`로 사용 |

공통 설정: JPA `ddl-auto: update`, SQL 로그 출력 및 포맷, 바인딩 파라미터 TRACE 로깅, `_method` 숨은 필드로 PUT/DELETE 표현(`spring.mvc.hiddenmethod.filter.enabled`).
dev 프로파일에서는 H2 콘솔(`/h2-console`)이 열려 있습니다.

H2 DB 파일(`*.mv.db`, `*.trace.db`, `*.lock.db`)은 `.gitignore`로 제외됩니다.
DB 계정 등 개인 설정값은 이 문서에 기록하지 않습니다.

### 실행 방법

```bash
./gradlew.bat bootRun
```

- 브라우저에서 http://localhost:8080 접속 (루트는 `/question/list`로 이동)
- 첫 기동 시 `TestInitData`가 샘플 데이터를 만듭니다: 회원 `admin`, `user1`(비밀번호는 `TestInitData.java` 참고, 로컬 샘플용), 질문 2건과 답변 2건, dev 프로파일에서는 페이징 확인용 질문 300건 추가
- 샘플 데이터를 처음부터 다시 만들려면 서버를 멈춘 뒤 프로젝트 루트의 `db_dev.mv.db`를 지우고 재기동
- 의존성을 바꾼 뒤에는 DevTools 재시작만으로 반영되지 않으므로 `bootRun`을 다시 실행

## 6. 기능 구현 현황

미션 1(교재 2장 ~ 3-13)의 기능이 모두 구현되어 있습니다. "검증"은 자동 테스트와 브라우저 수동 확인을 뜻합니다.

| 교재 절 | 기능 | 커밋 | 검증 |
|---|---|---|---|
| 2-01 ~ 2-12 | 프로젝트 설정, 엔티티, 리포지터리, 목록·상세, 답변 등록 | 000-01 | 테스트 |
| 2-13 ~ 2-15, 3-01 | 스타일, 부트스트랩, 공통 레이아웃, 내비게이션 바 | 002-01 | 테스트·브라우저 |
| 2-16 | 질문 등록 폼, 질문·답변 입력값 검증 | 003-01, 003-02 | 테스트·브라우저 |
| 3-02 ~ 3-04 | 페이징, 게시물 번호, 답변 개수 | 004-01, 004-02 | 테스트·브라우저 |
| 3-05 | Spring Security, CSRF, BCrypt | 005-01 | 테스트 |
| 3-06 | 회원가입 | 005-02 | 테스트·브라우저 |
| 3-07 | 로그인·로그아웃 | 006-01 | 테스트·브라우저 |
| 3-08 | 글쓴이 저장·표시, 로그인 사용자만 작성 | 007-01 | 테스트·브라우저 |
| 3-09 | 질문·답변 수정·삭제(PUT/DELETE), 소유자 검사 | 008-01, 008-02 | 테스트·브라우저 |
| 3-10 | 질문·답변 추천 | 009-01 | 테스트·브라우저 |
| 3-11 | 답변 앵커 | 010-01 | 테스트·브라우저 |
| 3-12 | 마크다운 렌더링(HTML 이스케이프) | 011-01 | 테스트·브라우저 |
| 3-13 | 검색 | 012-01 | 테스트·브라우저 |

### 미구현 (미션 2 범위)

- 3-14 추가 기능 9개 (답변 페이징·정렬, 댓글, 카테고리, 비밀번호 변경·찾기, 프로필, 조회 수, 소셜 로그인, 마크다운 에디터 등)
- 4장 배포

## 7. 교재 대비 개선 사항 (적용됨)

강사 14163·14168 강의에서 가져와 적용한 내용입니다. 각 항목의 근거는 작업내역의 "강사 코드와의 차이"에 있습니다.

- 폼 DTO를 record로 작성하고 `@Valid` + `BindingResult`로 검증 실패 시 폼을 다시 보여줌
- 오류 메시지를 "순번-필드명-메시지" 형식으로 적고 `form_errors.html`에서 정렬·첫 오류 필드 자동 포커스
- 수정·삭제·추천처럼 데이터를 바꾸는 요청은 GET 링크가 아니라 PUT/DELETE/POST + CSRF 토큰(`global.js`, `HiddenHttpMethodFilter`)
- 로그아웃도 POST 폼
- 부트스트랩은 저장소에 복사하지 않고 cdnjs 링크 사용, `layout:title-pattern`으로 페이지별 제목
- `@ManyToOne`은 모두 LAZY, `orphanRemoval`로 답변 삭제를 컬렉션에서 표현
- 마크다운 출력 시 HTML 이스케이프로 XSS 방지(교재에는 없음)
- 권한 오류는 400 대신 403
- 기동 시 샘플 회원·글 생성(`TestInitData`)과 dev 전용 대량 데이터

적용하지 않은 것: 강사 14168의 `BaseEntity`(`@MappedSuperclass` + Auditing) 공통화는 기존 엔티티 구조를 유지하기 위해 보류(미션 2 전에 검토)

## 8. 테스트 방법

테스트 코드는 `src/test/java/com/mysite/sbb` 아래에 있습니다.

| 클래스 | 대상 | 개수 |
|---|---|---|
| `SbbApplicationTests` | `QuestionRepository.findAll` | 1 |
| `QuestionRepositoryTest` | 질문 조회·수정·삭제, 답변 생성·조회 | 12 |
| `QuestionControllerTest` | 목록·페이징·검색, 등록·검증, 수정·삭제·추천과 권한, 마크다운 | 20 |
| `AnswerControllerTest` | 답변 등록·검증, 수정·삭제·추천과 권한, 앵커 | 10 |
| `UserControllerTest` | 회원가입 폼·성공·비밀번호 불일치·중복 ID·형식 오류 | 5 |
| `LoginTest` | 로그인 성공·실패, 로그아웃, 메뉴 분기 | 5 |
| `CommonUtilTest` | 마크다운 변환, HTML 이스케이프 | 3 |

테스트는 `test` 프로파일(인메모리 H2)을 사용하며, `@Transactional`로 각 테스트 후 롤백됩니다.
컨트롤러 테스트는 MockMvc 로 실제 뷰 렌더링까지 확인하고, POST/PUT/DELETE 에는 `csrf()`, 로그인이 필요한 요청에는 `user("user1")` 등을 붙입니다.

```bash
./gradlew.bat test
```

**현재 테스트 결과: 56개 전부 통과** (2026-11-13, 012-01 커밋 기준).

브라우저 수동 확인(기동 후): 목록·페이징·검색, 회원가입·로그인·로그아웃, 질문 등록·검증 오류, 글쓴이 표시, 수정(PUT)·삭제(DELETE), 추천, 답변 앵커, 마크다운·스크립트 이스케이프.

## 9. 시연 안내

1. 서버 기동 후 http://localhost:8080 접속. 목록·페이징·검색("sbb") 확인
2. 비로그인 상태에서 "질문 등록하기" 클릭 → 로그인 페이지로 이동하는 것 확인
3. 회원가입으로 새 계정 생성(또는 샘플 계정 `user1`로 로그인)
4. 질문 등록: 빈 폼 제출로 검증 오류 확인 → 정상 등록 → 목록에 글쓴이 표시
5. 상세에서 답변 등록(마크다운 `**굵게**`, 목록 문법) → 앵커로 이동, 렌더링 확인
6. 본인 글 수정("modified at" 표시)·삭제(confirm) / 다른 계정(`admin`)으로 로그인해 버튼이 없고 URL 직접 접근 시 403 확인
7. 추천 클릭 → 배지 증가, 다시 클릭해도 그대로
8. 로그아웃 → 메뉴가 로그인·회원가입으로 바뀜

주의: 브라우저의 `confirm()` 대화상자가 차단된 환경에서는 삭제·추천이 동작하지 않습니다.

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

### 006-01 로그인·로그아웃 기능

- 작업: `UserSecurityService`(`UserDetailsService` 구현, admin → ADMIN 권한), `SecurityConfig`에 `formLogin`(`/user/login`, 성공 시 `/`)과 `logout`(`/user/logout`, 세션 무효화) 설정, `UserController.login` GET, `login_form.html`, 내비게이션 바를 로그인 상태에 따라 로그인·회원가입 / 로그아웃(사용자명)으로 분기
- 이유: 교재 3-07 로그인·로그아웃. 이후 글쓴이 저장과 권한 검사의 전제
- 교재 대응: 3-07 로그인과 로그아웃 기능 구현하기
- 강사 코드와의 차이: 교재는 로그아웃을 GET 링크(`AntPathRequestMatcher`)로 허용하지만, 상태를 바꾸는 요청이므로 Spring Security 기본값대로 POST 폼(CSRF 토큰 포함)으로 보냄. 내비게이션 바에 `sec:authentication="name"`으로 현재 사용자명 표시
- 수정 파일: `user/UserSecurityService.java`(신규), `SecurityConfig.java`, `user/UserController.java`, `templates/login_form.html`(신규), `templates/navbar.html`, `test/.../LoginTest.java`(신규)
- 검증: `./gradlew test` 34개 통과(로그인 폼·성공·실패·로그아웃·메뉴 분기 테스트 5개). 서버 재시작 후 브라우저에서 테스트 계정 가입 → 로그인(메뉴에 `로그아웃 (demo1)` 표시) → 로그아웃까지 확인
- 학습 개념: `UserDetailsService`와 `DaoAuthenticationProvider`, `formLogin`/`logout` 설정, `sec:authorize`·`sec:authentication`, 로그아웃이 POST인 이유
- 제한 사항: 로그인 없이도 질문·답변 등록이 가능한 상태. 다음 커밋에서 `@PreAuthorize`로 제한

### 007-01 글쓴이 저장과 표시, 로그인 사용자만 작성 가능

- 작업: `Question`·`Answer`에 `@ManyToOne(fetch = LAZY) SiteUser author` 추가, `Question.addAnswer(content, author)`. `QuestionService.create`와 두 컨트롤러가 `Principal`로 로그인 사용자를 조회해 글쓴이로 저장(`UserService.getUser`). 등록 메서드에 `@PreAuthorize("isAuthenticated()")`. 목록에 글쓴이 열, 상세에 질문·답변 글쓴이 표시, 비로그인 사용자에게는 답변 입력창 비활성화. `TestInitData`가 샘플 회원 `admin`, `user1`을 만들고 샘플 글의 글쓴이로 지정
- 이유: 교재 3-08. 수정·삭제 권한 검사(3-09)의 전제가 되는 소유자 정보
- 교재 대응: 3-08 글쓴이 항목 추가하기
- 강사 코드와의 차이: 교재에는 샘플 회원이 없지만, 시연과 테스트의 편의를 위해 기동 시 샘플 회원을 생성(강사 14168 BaseInitData 방식). 비밀번호는 로컬 H2 샘플용으로 BCrypt 해시되어 저장됨. 기존 데이터의 null 글쓴이는 템플릿에서 null 검사로 처리(교재와 동일)
- 수정 파일: `question/Question.java`, `answer/Answer.java`, `question/QuestionService.java`, `question/QuestionController.java`, `answer/AnswerController.java`, `user/UserService.java`, `TestInitData.java`, `templates/question_list.html`, `templates/question_detail.html`, 테스트 4개 클래스
- 검증: `./gradlew test` 37개 통과(비로그인 등록 시 로그인 페이지로 리다이렉트, 글쓴이 저장·표시 테스트 추가). 브라우저에서 비로그인으로 `/question/create` 접근 시 로그인 페이지로 이동, `user1` 로그인 후 질문 등록 시 목록에 글쓴이 `user1` 표시, 상세에서 답변 글쓴이 `admin`/`user1` 표시 확인
- 학습 개념: `@ManyToOne` 단방향 연관관계와 FK, `Principal`에서 엔티티 조회, `@PreAuthorize`와 로그인 리다이렉트, `sec:authorize`로 입력창 분기
- 제한 사항: 기존 dev DB에는 `author_id`가 null인 글이 남을 수 있음(DB 파일 삭제 후 재기동하면 샘플 데이터가 새로 생성됨)

### 008-01 질문 수정·삭제, PUT/DELETE 방식

- 작업: `Question.modifyDate` 추가, `QuestionService.modify/delete`, `QuestionController`에 수정 폼 GET, 수정 `@PutMapping`, 삭제 `@DeleteMapping`과 글쓴이 검사(`checkOwner`, 403). `application.yaml`에 `spring.mvc.hiddenmethod.filter.enabled`, `static/resource/global.js`(a[method] 링크를 CSRF 토큰·`_method`가 담긴 POST 폼으로 변환)와 `layout.html`의 `_csrf` 메타·스크립트 추가. `question_form.html`을 등록·수정 겸용(`th:method`)으로, 상세 화면에 글쓴이 전용 수정·삭제 버튼과 "modified at" 표시
- 이유: 교재 3-09. 삭제를 GET 링크로 처리하는 교재 방식 대신, 상태를 바꾸는 요청은 PUT/DELETE + CSRF 로 보내는 강사 방식을 적용
- 교재 대응: 3-09 수정과 삭제 기능 추가하기
- 강사 코드와의 차이: 수정은 `@PutMapping`, 삭제는 `@DeleteMapping`(14168 41강). `global.js`와 `confirm` 처리(43~44강). 권한 오류는 교재의 400 대신 403. 폼 템플릿을 하나로 재사용하며 `modifying` 플래그로 제목·메서드를 바꿈
- 수정 파일: `question/Question.java`, `question/QuestionService.java`, `question/QuestionController.java`, `application.yaml`, `static/resource/global.js`(신규), `templates/layout.html`, `templates/question_form.html`, `templates/question_detail.html`, `test/.../QuestionControllerTest.java`
- 검증: `./gradlew test` 42개 통과(글쓴이 수정·삭제 성공, 타인 403, `_method=put` POST 처리, 버튼 노출 분기 테스트 5개 추가). 브라우저에서 `user1`로 질문 수정 → "modified at" 표시 확인. 삭제 링크는 `confirm` 후 `global.js`가 DELETE 폼을 전송함을 확인
- 학습 개념: HTML 폼은 GET/POST만 지원하므로 `_method`로 PUT/DELETE를 표현, `HiddenHttpMethodFilter`, CSRF 토큰을 JS 폼에 넣는 방법, 소유권 검사 위치(화면 + 서버)
- 제한 사항: 답변 수정·삭제는 다음 커밋

### 008-02 답변 수정·삭제

- 작업: `Answer.modifyDate`, `AnswerService`(조회·수정·삭제) 신설, `AnswerController`에 수정 폼 GET·`@PutMapping`·`@DeleteMapping`과 글쓴이 검사, `answer_form.html`, 상세 화면의 답변마다 글쓴이 전용 수정·삭제 버튼과 "modified at". `Question.answers`에 `orphanRemoval = true`와 `removeAnswer()` 추가
- 이유: 교재 3-09의 답변 부분. 답변 삭제 시 질문의 `answers` 목록에서도 빼야 cascade PERSIST 가 삭제를 되살리지 않음
- 교재 대응: 3-09 수정과 삭제 기능 추가하기(답변)
- 강사 코드와의 차이: 강사 14168 37강처럼 `orphanRemoval`과 컬렉션 제거로 삭제를 표현. PUT/DELETE + 403 은 008-01과 동일 규칙
- 수정 파일: `answer/Answer.java`, `answer/AnswerService.java`(신규), `answer/AnswerController.java`, `question/Question.java`, `templates/answer_form.html`(신규), `templates/question_detail.html`, `test/.../AnswerControllerTest.java`
- 검증: `./gradlew test` 46개 통과(글쓴이 수정, 타인 403, 삭제 후 질문의 답변 목록 반영, 빈 내용 거부 테스트 4개 추가). 삭제 링크의 JS 흐름은 008-01에서 동일 스크립트로 확인
- 학습 개념: `orphanRemoval`과 cascade PERSIST 의 상호작용, 양쪽(컬렉션·리포지터리)에서 일관되게 삭제하기, 서비스 계층의 `@Transactional`
- 제한 사항: 없음

### 009-01 질문·답변 추천

- 작업: `Question`·`Answer`에 `@ManyToMany Set<SiteUser> voter` 추가(조인 테이블 `question_voter`, `answer_voter`), `QuestionService.vote`·`AnswerService.vote`, 두 컨트롤러에 `@PostMapping("/vote/{id}")`, 상세 화면에 추천 버튼과 추천 수 배지
- 이유: 교재 3-10 추천 기능
- 교재 대응: 3-10 추천 기능 추가하기
- 강사 코드와의 차이: 교재는 GET `/question/vote/{id}` 링크지만 데이터를 바꾸므로 POST 로 받고, 화면에서는 `a[method="POST"]` 링크를 `global.js`가 CSRF 포함 폼으로 전송(008-01과 같은 방식). `Set` 덕분에 같은 사용자의 중복 추천은 한 번만 기록됨
- 수정 파일: `question/Question.java`, `answer/Answer.java`, `question/QuestionService.java`, `answer/AnswerService.java`, `question/QuestionController.java`, `answer/AnswerController.java`, `templates/question_detail.html`, 테스트 2개 클래스
- 검증: `./gradlew test` 49개 통과(추천 수 증가·중복 무시·비로그인 리다이렉트·답변 추천 테스트 3개 추가)
- 학습 개념: `@ManyToMany`와 조인 테이블, `Set`으로 중복 방지, 추천은 멱등하지 않은 요청이므로 POST
- 제한 사항: 추천 취소 기능은 교재 범위 밖(미션 2 후보)

### 010-01 답변 앵커

- 작업: 상세 화면의 답변마다 `<a id="answer_{id}">` 앵커 추가. 답변 등록·수정 후 `redirect:/question/detail/{qid}#answer_{aid}` 로 이동. 등록 시 새 답변 id 를 알기 위해 `AnswerService.flush()`로 INSERT 를 즉시 실행
- 이유: 교재 3-11. 답변이 많을 때 방금 쓴 답변 위치로 화면이 이동하도록
- 교재 대응: 3-11 앵커 기능 추가하기
- 강사 코드와의 차이: 교재는 `AnswerService.create`가 저장한 엔티티를 돌려주지만, 이 프로젝트는 cascade 로 저장하므로 커밋 전에는 id 가 0 이다. `flush()`로 INSERT 를 먼저 실행해 id 를 확정하는 방식으로 해결
- 수정 파일: `templates/question_detail.html`, `answer/AnswerController.java`, `answer/AnswerService.java`, `test/.../AnswerControllerTest.java`
- 검증: `./gradlew test` 50개 통과(리다이렉트 URL 이 실제 생성된 답변 id 의 앵커를 가리키는지 검증)
- 학습 개념: 영속성 컨텍스트의 쓰기 지연과 `flush()`, IDENTITY 전략에서 id 가 정해지는 시점, URL 프래그먼트(`#`)는 서버로 전송되지 않음
- 제한 사항: 없음

### 011-01 마크다운 렌더링

- 작업: `org.commonmark:commonmark` 의존성 추가, `CommonUtil.markdown()` 컴포넌트, 상세 화면의 질문·답변 본문을 `th:utext="${@commonUtil.markdown(...)}"`로 출력
- 이유: 교재 3-12. 본문에 목록·강조·링크 등 마크다운 문법을 쓸 수 있게 함
- 교재 대응: 3-12 마크다운 적용하기
- 강사 코드와의 차이: 교재는 입력된 HTML 을 그대로 통과시키지만, `HtmlRenderer.builder().escapeHtml(true)`로 본문에 직접 쓴 `<script>` 같은 태그를 문자로 바꿔 XSS 를 막음. `th:utext`는 이스케이프를 하지 않으므로 이 보호가 필요함
- 수정 파일: `build.gradle.kts`, `CommonUtil.java`(신규), `templates/question_detail.html`, `test/.../CommonUtilTest.java`(신규), `test/.../QuestionControllerTest.java`
- 검증: `./gradlew test` 54개 통과(마크다운 변환·HTML 이스케이프·null 처리 단위 테스트 3개, 상세 화면 렌더링 테스트 1개 추가)
- 학습 개념: `th:text` vs `th:utext`, 템플릿에서 빈 호출(`@beanName`), XSS 와 이스케이프
- 제한 사항: 새 의존성이므로 실행 중인 서버는 재시작 필요. 마크다운 에디터(미리보기)는 미션 2 범위

### 012-01 검색 기능

- 작업: `QuestionRepository.findAllByKeyword`(JPQL `@Query`, 제목·내용·질문 글쓴이·답변 내용·답변 글쓴이 `like`, `distinct`), `QuestionService.getList(page, kw)`, `QuestionController.list`의 `kw` 파라미터, 목록 화면의 검색창·찾기 버튼과 검색어를 유지하는 페이지 이동(숨은 GET 폼 + 스크립트)
- 이유: 교재 3-13 검색 기능
- 교재 대응: 3-13 검색 기능 추가하기
- 강사 코드와의 차이: 교재가 소개하는 두 방식(Specification, `@Query`) 중 읽기 쉬운 `@Query`(JPQL)를 채택. 검색창에서 Enter 로도 검색되도록 키 이벤트 추가. 조회이므로 폼은 GET 유지
- 수정 파일: `question/QuestionRepository.java`, `question/QuestionService.java`, `question/QuestionController.java`, `templates/question_list.html`, `test/.../QuestionControllerTest.java`
- 검증: `./gradlew test` 56개 통과(제목·답변 내용·답변 글쓴이·질문 글쓴이·없는 단어 검색 건수, 검색어 유지 테스트 2개 추가)
- 학습 개념: JPQL `left outer join ... on`, `distinct` 가 필요한 이유(1:N 조인의 행 중복), `Page` 와 검색 조건의 결합, 조회 요청은 GET 으로
- 제한 사항: `like %kw%` 는 인덱스를 타지 못하므로 데이터가 많아지면 전문 검색 도입을 고려

### 013-01 문서, 미션 1 완료 현황 반영

- 작업: README 4~8절을 현재 구현에 맞게 갱신(기술 스택, 실행 방법, 기능 현황표, 적용된 개선 사항, 테스트 현황 56개) 하고 9절 시연 안내 추가
- 이유: README 가 실제 구현과 일치해야 하며, 영상 제작 전 시연 순서를 정리
- 교재 대응: 전체(2-13 ~ 3-13)
- 강사 코드와의 차이: 해당 없음(문서)
- 수정 파일: `README.md`
- 검증: `./gradlew test` 56개 통과 상태에서 문서만 변경
- 학습 개념: 커밋별 복습 순서는 작업내역의 번호 순서를 따르면 됨
- 제한 사항: 미션 2(3-14)와 배포(4장)는 범위 밖
