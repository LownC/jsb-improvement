package com.mysite.sbb.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 로그인 시 Spring Security 가 사용자 정보를 조회하는 통로. (교재 3-07)
 * 비밀번호 비교는 SecurityConfig 의 PasswordEncoder 가 자동으로 수행한다.
 */
@Service
@RequiredArgsConstructor
public class UserSecurityService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SiteUser siteUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // 교재 규칙: 사용자ID가 "admin" 이면 ADMIN, 나머지는 USER 권한
        UserRole role = "admin".equals(username) ? UserRole.ADMIN : UserRole.USER;
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role.getValue()));

        return new User(siteUser.getUsername(), siteUser.getPassword(), authorities);
    }
}
