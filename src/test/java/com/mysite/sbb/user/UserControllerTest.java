package com.mysite.sbb.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입 폼이 표시된다")
    void t1() throws Exception {
        mvc.perform(get("/user/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup_form"));
    }

    @Test
    @DisplayName("회원가입하면 비밀번호가 해시로 저장되고 루트로 리다이렉트된다")
    void t2() throws Exception {
        mvc.perform(post("/user/signup")
                        .with(csrf())
                        .param("username", "tester")
                        .param("password1", "pass1234")
                        .param("password2", "pass1234")
                        .param("email", "tester@test.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        SiteUser user = userRepository.findByUsername("tester").orElseThrow();
        assertThat(user.getPassword()).isNotEqualTo("pass1234");
        assertThat(passwordEncoder.matches("pass1234", user.getPassword())).isTrue();
        assertThat(user.getEmail()).isEqualTo("tester@test.com");
    }

    @Test
    @DisplayName("두 비밀번호가 다르면 가입이 거부된다")
    void t3() throws Exception {
        mvc.perform(post("/user/signup")
                        .with(csrf())
                        .param("username", "tester")
                        .param("password1", "pass1234")
                        .param("password2", "different")
                        .param("email", "tester@test.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup_form"))
                .andExpect(model().attributeHasFieldErrors("form", "password2"))
                .andExpect(content().string(containsString("2개의 비밀번호가 일치하지 않습니다.")));

        assertThat(userRepository.findByUsername("tester")).isEmpty();
    }

    @Test
    @DisplayName("이미 있는 사용자ID로는 가입할 수 없다")
    void t4() throws Exception {
        long before = userRepository.count();
        mvc.perform(post("/user/signup").with(csrf())
                .param("username", "tester").param("password1", "pass1234")
                .param("password2", "pass1234").param("email", "tester@test.com"));

        mvc.perform(post("/user/signup")
                        .with(csrf())
                        .param("username", "tester")
                        .param("password1", "other")
                        .param("password2", "other")
                        .param("email", "other@test.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup_form"))
                .andExpect(content().string(containsString("이미 등록된 사용자ID입니다.")));

        assertThat(userRepository.count()).isEqualTo(before + 1);
    }

    @Test
    @DisplayName("짧은 ID와 잘못된 이메일은 검증에 걸린다")
    void t5() throws Exception {
        mvc.perform(post("/user/signup")
                        .with(csrf())
                        .param("username", "ab")
                        .param("password1", "pass1234")
                        .param("password2", "pass1234")
                        .param("email", "not-an-email"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("form", "username", "email"));
    }
}
