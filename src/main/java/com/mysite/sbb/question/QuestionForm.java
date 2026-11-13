package com.mysite.sbb.question;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 질문 등록·수정 폼.
 * 교재는 Lombok @Getter/@Setter 클래스를 쓰지만, 강사 14168 40강처럼 record로 작성해 코드량을 줄였다.
 * 메시지 형식 "순번-필드명-메시지"는 form_errors.html 프래그먼트가 정렬과 포커스에 사용한다.
 */
public record QuestionForm(
        @NotBlank(message = "01-subject-제목은 필수항목입니다.")
        @Size(max = 200, message = "02-subject-제목은 200자 이하로 입력해주세요.")
        String subject,

        @NotBlank(message = "03-content-내용은 필수항목입니다.")
        String content
) {
}
