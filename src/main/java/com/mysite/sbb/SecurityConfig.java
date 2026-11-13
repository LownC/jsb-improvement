package com.mysite.sbb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 교재 3-05 스프링 시큐리티 설정.
 * - 모든 URL은 일단 허용하고, 로그인이 필요한 메서드는 @PreAuthorize 로 개별 보호한다. (교재 방식)
 * - CSRF 보호는 켜 둔다. Thymeleaf 의 th:action 폼에는 토큰이 자동으로 들어간다.
 * - H2 콘솔은 프레임을 쓰므로 CSRF 예외와 같은 출처 프레임 허용이 필요하다.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**").permitAll()
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**")
                )
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )
                // 교재 3-07: 로그인 페이지는 /user/login, 성공하면 루트로. 로그인 처리(POST /user/login)는 시큐리티가 담당한다.
                .formLogin(formLogin -> formLogin
                        .loginPage("/user/login")
                        .defaultSuccessUrl("/")
                )
                // 로그아웃은 POST /user/logout (CSRF 토큰 필요) 후 루트로 이동, 세션 무효화
                .logout(logout -> logout
                        .logoutUrl("/user/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                );

        return http.build();
    }

    // 비밀번호는 평문으로 저장하지 않고 BCrypt 해시로 저장한다. (교재 3-06)
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
