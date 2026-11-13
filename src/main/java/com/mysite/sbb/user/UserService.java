package com.mysite.sbb.user;

import com.mysite.sbb.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    // SecurityConfig 에 등록한 BCryptPasswordEncoder 빈을 주입받는다. (교재 3-06)
    private final PasswordEncoder passwordEncoder;

    public SiteUser create(String username, String email, String password) {
        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setEmail(email);
        // 비밀번호는 반드시 해시로 저장한다. 같은 비밀번호라도 저장값은 매번 다르다.
        user.setPassword(passwordEncoder.encode(password));

        return userRepository.save(user);
    }

    public Optional<SiteUser> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // 로그인한 사용자명(Principal)으로 엔티티를 가져올 때 사용. 없으면 404. (교재 3-08)
    public SiteUser getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new DataNotFoundException("siteuser not found"));
    }
}
