package com.mysite.sbb.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/signup")
    public String signup(@ModelAttribute("form") UserCreateForm form) {
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(
            @ModelAttribute("form") @Valid UserCreateForm form,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }

        // 어노테이션으로 표현하기 어려운 검증은 직접 BindingResult 에 오류를 추가한다. (교재 3-06)
        if (!form.password1().equals(form.password2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "05-password2-2개의 비밀번호가 일치하지 않습니다.");
            return "signup_form";
        }

        if (userService.findByUsername(form.username()).isPresent()) {
            bindingResult.rejectValue("username", "duplicateUsername",
                    "02-username-이미 등록된 사용자ID입니다.");
            return "signup_form";
        }

        try {
            userService.create(form.username(), form.email(), form.password1());
        } catch (DataIntegrityViolationException e) {
            // 이메일 중복 등 DB 유니크 제약 위반은 저장 시점에야 알 수 있다.
            bindingResult.reject("signupFailed", "08-email-이미 등록된 사용자ID 또는 이메일입니다.");
            return "signup_form";
        }

        return "redirect:/";
    }

    // GET 은 로그인 화면만 보여준다. POST /user/login 은 Spring Security 가 처리하므로 메서드가 필요 없다. (교재 3-07)
    @GetMapping("/login")
    public String login() {
        return "login_form";
    }
}
