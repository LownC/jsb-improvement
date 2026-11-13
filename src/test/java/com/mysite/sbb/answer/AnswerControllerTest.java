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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    @Autowired
    private AnswerRepository answerRepository;

    @Test
    @DisplayName("답변을 등록하면 질문 상세로 리다이렉트되고 답변이 늘어난다")
    void t1() throws Exception {
        Question question = questionRepository.findById(1).get();
        int before = question.getAnswers().size();

        mvc.perform(post("/answer/create/1")
                        .with(csrf()).with(user("user1"))
                        .param("content", "첫 번째 답변"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/question/detail/1#answer_*"));

        assertThat(question.getAnswers()).hasSize(before + 1);
        assertThat(question.getAnswers().get(before).getContent()).isEqualTo("첫 번째 답변");
        assertThat(question.getAnswers().get(before).getAuthor().getUsername()).isEqualTo("user1");
    }

    @Test
    @DisplayName("빈 답변은 거부되고 상세 화면에 오류가 표시된다")
    void t2() throws Exception {
        Question question = questionRepository.findById(1).get();
        int before = question.getAnswers().size();

        mvc.perform(post("/answer/create/1")
                        .with(csrf()).with(user("user1"))
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
                        .with(csrf()).with(user("user1"))
                        .param("content", "답변"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("로그인하지 않으면 답변을 등록할 수 없다")
    void t4() throws Exception {
        Question question = questionRepository.findById(1).get();
        int before = question.getAnswers().size();

        mvc.perform(post("/answer/create/1").with(csrf())
                        .param("content", "익명 답변"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/user/login"));

        assertThat(question.getAnswers()).hasSize(before);
    }

    @Test
    @DisplayName("답변 글쓴이는 답변을 수정할 수 있다 (답변 2의 글쓴이는 user1)")
    void t5() throws Exception {
        mvc.perform(get("/answer/modify/2").with(user("user1")))
                .andExpect(status().isOk())
                .andExpect(view().name("answer_form"))
                .andExpect(content().string(containsString("맞습니다!")));

        mvc.perform(put("/answer/modify/2").with(csrf()).with(user("user1"))
                        .param("content", "수정된 답변"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/question/detail/2#answer_2"));

        Answer answer = answerRepository.findById(2).get();
        assertThat(answer.getContent()).isEqualTo("수정된 답변");
        assertThat(answer.getModifyDate()).isNotNull();
    }

    @Test
    @DisplayName("답변 글쓴이가 아니면 수정·삭제는 403 (답변 1의 글쓴이는 admin)")
    void t6() throws Exception {
        mvc.perform(get("/answer/modify/1").with(user("user1")))
                .andExpect(status().isForbidden());

        mvc.perform(delete("/answer/delete/1").with(csrf()).with(user("user1")))
                .andExpect(status().isForbidden());

        assertThat(answerRepository.findById(1)).isPresent();
    }

    @Test
    @DisplayName("답변 글쓴이는 답변을 삭제할 수 있고 질문의 답변 목록에서도 빠진다")
    void t7() throws Exception {
        mvc.perform(delete("/answer/delete/1").with(csrf()).with(user("admin")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/question/detail/2"));

        assertThat(answerRepository.findById(1)).isEmpty();
        Question question = questionRepository.findById(2).get();
        assertThat(question.getAnswers()).hasSize(1);
        assertThat(question.getAnswers().get(0).getId()).isEqualTo(2);
    }

    @Test
    @DisplayName("빈 내용으로 수정하면 폼이 다시 표시된다")
    void t8() throws Exception {
        mvc.perform(put("/answer/modify/2").with(csrf()).with(user("user1"))
                        .param("content", " "))
                .andExpect(status().isOk())
                .andExpect(view().name("answer_form"))
                .andExpect(model().attributeHasFieldErrors("form", "content"));
    }

    @Test
    @DisplayName("답변을 추천하면 추천인이 기록되고 질문 상세로 돌아간다")
    void t9() throws Exception {
        mvc.perform(post("/answer/vote/1").with(csrf()).with(user("user1")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/question/detail/2"));

        assertThat(answerRepository.findById(1).get().getVoter()).hasSize(1);
    }

    @Test
    @DisplayName("상세 화면의 답변마다 앵커가 있고, 등록 후 리다이렉트 URL 이 새 답변의 앵커를 가리킨다")
    void t10() throws Exception {
        mvc.perform(get("/question/detail/2"))
                .andExpect(content().string(containsString("id=\"answer_1\"")))
                .andExpect(content().string(containsString("id=\"answer_2\"")));

        String location = mvc.perform(post("/answer/create/2").with(csrf()).with(user("user1"))
                        .param("content", "앵커 확인 답변"))
                .andReturn().getResponse().getRedirectedUrl();
        Question question = questionRepository.findById(2).get();
        Answer created = question.getAnswers().get(question.getAnswers().size() - 1);
        assertThat(created.getId()).isGreaterThan(0);
        assertThat(location).isEqualTo("/question/detail/2#answer_" + created.getId());
    }
}
