package com.mysite.sbb.answer;

import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/answer")
public class AnswerController {
    private final QuestionService questionService;

    // 컨트롤러 메서드에 @Transactional 을 두면 question.addAnswer() 로 추가된 답변이
    // 트랜잭션 종료 시 cascade(PERSIST)로 함께 저장된다. (강사 14163 37강)
    @PostMapping("/create/{id}")
    @Transactional
    public String createAnswer(
            Model model,
            @PathVariable int id,
            @ModelAttribute("answerForm") @Valid AnswerForm answerForm,
            BindingResult bindingResult
    ) {
        Question question = questionService.getQuestion(id);

        if (bindingResult.hasErrors()) {
            // 검증 실패 시 질문 상세 화면을 그대로 다시 그린다. (교재 2-16)
            model.addAttribute("question", question);
            return "question_detail";
        }

        question.addAnswer(answerForm.content());

        return String.format("redirect:/question/detail/%s", id);
    }
}
