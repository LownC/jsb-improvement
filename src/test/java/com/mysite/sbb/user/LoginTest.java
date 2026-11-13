package com.mysite.sbb.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LoginTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService.create("tester", "tester@test.com", "pass1234");
    }

    @Test
    @DisplayName("로그인 폼이 표시되고 비로그인 상태에서는 로그인 링크가 보인다")
    void t1() throws Exception {
        mvc.perform(get("/user/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login_form"))
                .andExpect(content().string(containsString("/user/login")))
                .andExpect(content().string(containsString("회원가입")));
    }

    @Test
    @DisplayName("올바른 ID와 비밀번호로 로그인하면 루트로 이동하고 인증된다")
    void t2() throws Exception {
        mvc.perform(formLogin("/user/login").user("tester").password("pass1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("tester"));
    }

    @Test
    @DisplayName("틀린 비밀번호로는 로그인되지 않고 ?error 로 돌아온다")
    void t3() throws Exception {
        mvc.perform(formLogin("/user/login").user("tester").password("wrong"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/login?error"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("로그인 사용자에게는 로그아웃 버튼과 이름이 보인다")
    void t4() throws Exception {
        mvc.perform(get("/question/list").with(user("tester")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("로그아웃")))
                .andExpect(content().string(containsString("tester")));
    }

    @Test
    @DisplayName("로그아웃하면 루트로 이동하고 인증이 해제된다")
    void t5() throws Exception {
        mvc.perform(logout("/user/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(unauthenticated());
    }
}
