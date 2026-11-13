package com.mysite.sbb.answer;

import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AnswerControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    @DisplayName("답변을 등록하면 질문 상세로 리다이렉트되고 답변이 늘어난다")
    void t1() throws Exception {
        Question question = questionRepository.findById(1).get();
        int before = question.getAnswers().size();

        mvc.perform(post("/answer/create/1")
                        .param("content", "첫 번째 답변"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/question/detail/1"));

        assertThat(question.getAnswers()).hasSize(before + 1);
        assertThat(question.getAnswers().get(before).getContent()).isEqualTo("첫 번째 답변");
    }

    @Test
    @DisplayName("빈 답변은 거부되고 상세 화면에 오류가 표시된다")
    void t2() throws Exception {
        Question question = questionRepository.findById(1).get();
        int before = question.getAnswers().size();

        mvc.perform(post("/answer/create/1")
                        .param("content", "   "))
                .andExpect(status().isOk())
                .andExpect(view().name("question_detail"))
                .andExpect(model().attributeHasFieldErrors("answerForm", "content"))
                .andExpect(content().string(containsString("내용은 필수항목입니다.")));

        assertThat(question.getAnswers()).hasSize(before);
    }

    @Test
    @DisplayName("없는 질문에 답변하면 404")
    void t3() throws Exception {
        mvc.perform(post("/answer/create/9999")
                        .param("content", "답변"))
                .andExpect(status().isNotFound());
    }
}
