package com.mysite.sbb.question;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/question")
public class QuestionController {
    private final QuestionService questionService;

    @GetMapping("/list")
    public String list(Model model) {
        List<Question> questionList = questionService.getList();
        model.addAttribute("questionList", questionList);

        return "question_list";
    }

    @GetMapping("/detail/{id}")
    public String detail(Model model, @PathVariable int id) {
        Question question = questionService.getQuestion(id);

        model.addAttribute("question", question);
        return "question_detail";
    }

    // 빈 폼 객체를 "form" 이름으로 모델에 실어 템플릿의 th:object="${form}"이 동작하게 한다.
    @GetMapping("/create")
    public String questionCreate(@ModelAttribute("form") QuestionForm form) {
        return "question_form";
    }

    // @Valid 로 검증하고, 실패하면 예외 대신 BindingResult 에 담겨 폼을 다시 보여줄 수 있다. (강사 14168 13강)
    @PostMapping("/create")
    public String questionCreate(
            @ModelAttribute("form") @Valid QuestionForm form,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }

        questionService.create(form.subject(), form.content());

        return "redirect:/question/list";
    }
}
