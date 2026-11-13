package com.mysite.sbb.question;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class QuestionControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    @DisplayName("질문 목록 페이지가 레이아웃과 함께 렌더링된다")
    void t1() throws Exception {
        mvc.perform(get("/question/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("question_list"))
                .andExpect(content().string(containsString("navbar")))
                .andExpect(content().string(containsString("sbb가 무엇인가요?")));
    }

    @Test
    @DisplayName("질문 상세 페이지에 답변 개수가 표시된다")
    void t2() throws Exception {
        mvc.perform(get("/question/detail/2"))
                .andExpect(status().isOk())
                .andExpect(view().name("question_detail"))
                .andExpect(content().string(containsString("2개의 답변이 있습니다.")));
    }

    @Test
    @DisplayName("없는 질문은 404를 돌려준다")
    void t3() throws Exception {
        mvc.perform(get("/question/detail/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("루트 URL은 질문 목록으로 리다이렉트된다")
    void t4() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/question/list"));
    }

    @Test
    @DisplayName("목록은 페이지 단위(10건, 최신순)로 조회된다")
    void t1_paging() throws Exception {
        mvc.perform(get("/question/list").param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("paging"))
                .andExpect(content().string(containsString("pagination")))
                // 답변이 2개인 질문에는 답변 개수 배지가 표시된다 (교재 3-04)
                .andExpect(content().string(containsString("text-danger small ms-2")));

        // 범위를 벗어난 페이지는 빈 페이지로 응답하고 오류가 나지 않는다
        mvc.perform(get("/question/list").param("page", "99"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("질문 등록 폼이 표시된다")
    void t5() throws Exception {
        mvc.perform(get("/question/create").with(user("user1")))
                .andExpect(status().isOk())
                .andExpect(view().name("question_form"))
                .andExpect(content().string(containsString("질문 등록")));
    }

    @Test
    @DisplayName("질문을 등록하면 목록으로 리다이렉트되고 저장된다")
    void t6() throws Exception {
        long before = questionRepository.count();

        mvc.perform(post("/question/create")
                        .with(csrf()).with(user("user1"))
                        .param("subject", "새 질문")
                        .param("content", "새 내용"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/question/list"));

        assertThat(questionRepository.count()).isEqualTo(before + 1);
        Question saved = questionRepository.findBySubject("새 질문").orElseThrow();
        assertThat(saved.getAuthor().getUsername()).isEqualTo("user1");
    }

    @Test
    @DisplayName("제목이 비어 있으면 폼이 다시 표시되고 오류 메시지가 나온다")
    void t7() throws Exception {
        long before = questionRepository.count();

        mvc.perform(post("/question/create")
                        .with(csrf()).with(user("user1"))
                        .param("subject", "")
                        .param("content", "내용만 있음"))
                .andExpect(status().isOk())
                .andExpect(view().name("question_form"))
                .andExpect(model().attributeHasFieldErrors("form", "subject"))
                .andExpect(content().string(containsString("제목은 필수항목입니다.")));

        assertThat(questionRepository.count()).isEqualTo(before);
    }

    @Test
    @DisplayName("로그인하지 않으면 질문 등록 폼 대신 로그인 페이지로 보낸다")
    void t8() throws Exception {
        mvc.perform(get("/question/create"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/user/login"));

        mvc.perform(post("/question/create").with(csrf())
                        .param("subject", "익명 질문").param("content", "내용"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/user/login"));

        assertThat(questionRepository.findBySubject("익명 질문")).isEmpty();
    }

    @Test
    @DisplayName("목록과 상세에 글쓴이가 표시된다")
    void t9() throws Exception {
        mvc.perform(get("/question/list"))
                .andExpect(content().string(containsString("글쓴이")))
                .andExpect(content().string(containsString("user1")));

        mvc.perform(get("/question/detail/2"))
                .andExpect(content().string(containsString("admin")));
    }

    @Test
    @DisplayName("글쓴이는 질문을 수정할 수 있고 modifyDate 가 기록된다")
    void t10() throws Exception {
        // 1번 질문의 글쓴이는 user1
        mvc.perform(get("/question/modify/1").with(user("user1")))
                .andExpect(status().isOk())
                .andExpect(view().name("question_form"))
                .andExpect(content().string(containsString("sbb가 무엇인가요?")));

        mvc.perform(put("/question/modify/1").with(csrf()).with(user("user1"))
                        .param("subject", "수정된 제목").param("content", "수정된 내용"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/question/detail/1"));

        Question question = questionRepository.findById(1).get();
        assertThat(question.getSubject()).isEqualTo("수정된 제목");
        assertThat(question.getModifyDate()).isNotNull();
    }

    @Test
    @DisplayName("글쓴이가 아니면 수정·삭제는 403")
    void t11() throws Exception {
        mvc.perform(get("/question/modify/1").with(user("admin")))
                .andExpect(status().isForbidden());

        mvc.perform(put("/question/modify/1").with(csrf()).with(user("admin"))
                        .param("subject", "남의 글").param("content", "수정 시도"))
                .andExpect(status().isForbidden());

        mvc.perform(delete("/question/delete/1").with(csrf()).with(user("admin")))
                .andExpect(status().isForbidden());

        assertThat(questionRepository.findById(1)).isPresent();
    }

    @Test
    @DisplayName("글쓴이는 질문을 삭제할 수 있고 답변도 함께 지워진다")
    void t12() throws Exception {
        long before = questionRepository.count();

        mvc.perform(delete("/question/delete/2").with(csrf()).with(user("user1")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        assertThat(questionRepository.count()).isEqualTo(before - 1);
        assertThat(questionRepository.findById(2)).isEmpty();
    }

    @Test
    @DisplayName("폼의 _method=put 으로 보낸 POST 도 수정으로 처리된다")
    void t13() throws Exception {
        mvc.perform(post("/question/modify/1").with(csrf()).with(user("user1"))
                        .param("_method", "put")
                        .param("subject", "폼으로 수정").param("content", "내용"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/question/detail/1"));

        assertThat(questionRepository.findById(1).get().getSubject()).isEqualTo("폼으로 수정");
    }

    @Test
    @DisplayName("상세 화면의 수정·삭제 버튼은 글쓴이에게만 보인다")
    void t14() throws Exception {
        mvc.perform(get("/question/detail/1").with(user("user1")))
                .andExpect(content().string(containsString("/question/modify/1")))
                .andExpect(content().string(containsString("method=\"DELETE\"")));

        mvc.perform(get("/question/detail/1").with(user("admin")))
                .andExpect(content().string(org.hamcrest.Matchers.not(containsString("/question/modify/1"))));
    }

    @Test
    @DisplayName("추천하면 추천인이 늘고, 같은 사용자가 다시 추천해도 늘지 않는다")
    void t15() throws Exception {
        mvc.perform(post("/question/vote/1").with(csrf()).with(user("admin")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/question/detail/1"));
        assertThat(questionRepository.findById(1).get().getVoter()).hasSize(1);

        mvc.perform(post("/question/vote/1").with(csrf()).with(user("admin")));
        assertThat(questionRepository.findById(1).get().getVoter()).hasSize(1);

        mvc.perform(post("/question/vote/1").with(csrf()).with(user("user1")));
        assertThat(questionRepository.findById(1).get().getVoter()).hasSize(2);

        mvc.perform(get("/question/detail/1"))
                .andExpect(content().string(containsString("badge rounded-pill bg-success\">2")));
    }

    @Test
    @DisplayName("로그인하지 않으면 추천할 수 없다")
    void t16() throws Exception {
        mvc.perform(post("/question/vote/1").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/user/login"));
        assertThat(questionRepository.findById(1).get().getVoter()).isEmpty();
    }

    @Test
    @DisplayName("질문 본문의 마크다운이 HTML 로 렌더링된다")
    void t17() throws Exception {
        mvc.perform(put("/question/modify/1").with(csrf()).with(user("user1"))
                .param("subject", "마크다운").param("content", "**굵은 글씨**"));

        mvc.perform(get("/question/detail/1"))
                .andExpect(content().string(containsString("<strong>굵은 글씨</strong>")));
    }
}
