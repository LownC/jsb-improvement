package com.mysite.sbb.answer;

import jakarta.validation.constraints.NotBlank;

/**
 * 답변 등록·수정 폼. QuestionForm과 같은 규칙(record, "순번-필드명-메시지")을 따른다.
 */
public record AnswerForm(
        @NotBlank(message = "01-content-내용은 필수항목입니다.")
        String content
) {
}
