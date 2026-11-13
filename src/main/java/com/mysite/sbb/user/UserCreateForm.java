package com.mysite.sbb.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 회원가입 폼. (교재 3-06, record 와 "순번-필드명-메시지" 규칙은 QuestionForm 과 동일)
 */
public record UserCreateForm(
        @NotBlank(message = "01-username-사용자ID는 필수항목입니다.")
        @Size(min = 3, max = 25, message = "02-username-사용자ID는 3자 이상 25자 이하로 입력해주세요.")
        String username,

        @NotBlank(message = "03-password1-비밀번호는 필수항목입니다.")
        String password1,

        @NotBlank(message = "04-password2-비밀번호 확인은 필수항목입니다.")
        String password2,

        @NotBlank(message = "06-email-이메일은 필수항목입니다.")
        @Email(message = "07-email-올바른 이메일 형식이 아닙니다.")
        String email
) {
}
