package com.mysite.sbb.question;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class QuestionControllerTest {
    @Autowired
    private MockMvc mvc;

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
}
