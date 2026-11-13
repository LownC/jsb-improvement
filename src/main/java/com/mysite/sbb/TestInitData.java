package com.mysite.sbb;

import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class TestInitData {
    @Autowired
    @Lazy
    private TestInitData self;
    private final QuestionRepository questionRepository;
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

        Question q1 = new Question();
        q1.setSubject("sbb가 무엇인가요?");
        q1.setContent("sbb에 대해서 알고 싶습니다.");
        q1.setCreateDate(LocalDateTime.now());
        questionRepository.save(q1); // 첫번째 질문 저장

        Question q2 = new Question();
        q2.setSubject("스프링부트 모델 질문입니다.");
        q2.setContent("id는 자동으로 생성되나요?");
        q2.setCreateDate(LocalDateTime.now());

        q2.addAnswer("네 자동으로 생성됩니다.");
        q2.addAnswer("맞습니다!");

        questionRepository.save(q2); // 두번째 질문 저장

        // 페이징 확인용 대량 데이터는 dev 프로파일에서만 만든다. (교재 3-02는 테스트 코드로 300건 생성)
        // test 프로파일은 테스트들이 "질문 2건"을 전제로 하므로 제외한다.
        if (environment.matchesProfiles("dev")) {
            for (int i = 1; i <= 300; i++) {
                Question q = new Question();
                q.setSubject(String.format("테스트 데이터입니다:[%03d]", i));
                q.setContent("내용무");
                q.setCreateDate(LocalDateTime.now());
                questionRepository.save(q);
            }
        }
    }
}