package com.mysite.sbb;

import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionRepository;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserRepository;
import com.mysite.sbb.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 기동 시 샘플 데이터를 넣는다. (강사 14163 14강 방식: @Lazy self 로 @Transactional 프록시를 통해 호출)
 * 데이터가 이미 있으면 아무것도 하지 않는다.
 */
@Configuration
@RequiredArgsConstructor
public class TestInitData {
    @Autowired
    @Lazy
    private TestInitData self;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final Environment environment;

    @Bean
    public ApplicationRunner testInitDataApplicationRunner() {
        return args -> {
            self.work1();
        };
    }

    @Transactional
    void work1() {
        if (questionRepository.count() > 0) return;

        // 샘플 회원. 비밀번호는 로컬 H2 샘플용이며 기동 시 BCrypt 로 해시되어 저장된다.
        // admin 은 UserSecurityService 규칙에 따라 ADMIN 권한을 받는다.
        SiteUser admin = userRepository.findByUsername("admin")
                .orElseGet(() -> userService.create("admin", "admin@test.com", "1234"));
        SiteUser user1 = userRepository.findByUsername("user1")
                .orElseGet(() -> userService.create("user1", "user1@test.com", "1234"));

        Question q1 = new Question();
        q1.setSubject("sbb가 무엇인가요?");
        q1.setContent("sbb에 대해서 알고 싶습니다.");
        q1.setCreateDate(LocalDateTime.now());
        q1.setAuthor(user1);
        questionRepository.save(q1); // 첫번째 질문 저장

        Question q2 = new Question();
        q2.setSubject("스프링부트 모델 질문입니다.");
        q2.setContent("id는 자동으로 생성되나요?");
        q2.setCreateDate(LocalDateTime.now());
        q2.setAuthor(user1);

        q2.addAnswer("네 자동으로 생성됩니다.", admin);
        q2.addAnswer("맞습니다!", user1);

        questionRepository.save(q2); // 두번째 질문 저장

        // 페이징 확인용 대량 데이터는 dev 프로파일에서만 만든다. (교재 3-02는 테스트 코드로 300건 생성)
        // test 프로파일은 테스트들이 "질문 2건"을 전제로 하므로 제외한다.
        if (environment.matchesProfiles("dev")) {
            for (int i = 1; i <= 300; i++) {
                Question q = new Question();
                q.setSubject(String.format("테스트 데이터입니다:[%03d]", i));
                q.setContent("내용무");
                q.setCreateDate(LocalDateTime.now());
                q.setAuthor(i % 2 == 0 ? admin : user1);
                questionRepository.save(q);
            }
        }
    }
}
