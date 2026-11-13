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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
                .andExpect(content().string(containsString("pagination")));

        // 범위를 벗어난 페이지는 빈 페이지로 응답하고 오류가 나지 않는다
        mvc.perform(get("/question/list").param("page", "99"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("질문 등록 폼이 표시된다")
    void t5() throws Exception {
        mvc.perform(get("/question/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("question_form"))
                .andExpect(content().string(containsString("질문등록")));
    }

    @Test
    @DisplayName("질문을 등록하면 목록으로 리다이렉트되고 저장된다")
    void t6() throws Exception {
        long before = questionRepository.count();

        mvc.perform(post("/question/create")
                        .param("subject", "새 질문")
                        .param("content", "새 내용"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/question/list"));

        assertThat(questionRepository.count()).isEqualTo(before + 1);
        assertThat(questionRepository.findBySubject("새 질문")).isPresent();
    }

    @Test
    @DisplayName("제목이 비어 있으면 폼이 다시 표시되고 오류 메시지가 나온다")
    void t7() throws Exception {
        long before = questionRepository.count();

        mvc.perform(post("/question/create")
                        .param("subject", "")
                        .param("content", "내용만 있음"))
                .andExpect(status().isOk())
                .andExpect(view().name("question_form"))
                .andExpect(model().attributeHasFieldErrors("form", "subject"))
                .andExpect(content().string(containsString("제목은 필수항목입니다.")));

        assertThat(questionRepository.count()).isEqualTo(before);
    }
}
