package com.mysite.sbb.answer;

import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/answer")
public class AnswerController {
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;

    // 컨트롤러 메서드에 @Transactional 을 두면 question.addAnswer() 로 추가된 답변이
    // 트랜잭션 종료 시 cascade(PERSIST)로 함께 저장된다. (강사 14163 37강)
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    @Transactional
    public String createAnswer(
            Model model,
            @PathVariable int id,
            @ModelAttribute("answerForm") @Valid AnswerForm answerForm,
            BindingResult bindingResult,
            Principal principal
    ) {
        Question question = questionService.getQuestion(id);

        if (bindingResult.hasErrors()) {
            // 검증 실패 시 질문 상세 화면을 그대로 다시 그린다. (교재 2-16)
            model.addAttribute("question", question);
            return "question_detail";
        }

        SiteUser author = userService.getUser(principal.getName());
        question.addAnswer(answerForm.content(), author);

        return String.format("redirect:/question/detail/%s", id);
    }

    // 교재 3-09: 답변 수정 폼
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String answerModify(Model model, @PathVariable int id, Principal principal) {
        Answer answer = answerService.getAnswer(id);
        checkOwner(answer, principal);

        model.addAttribute("form", new AnswerForm(answer.getContent()));
        return "answer_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/modify/{id}")
    public String answerModify(
            @PathVariable int id,
            @ModelAttribute("form") @Valid AnswerForm form,
            BindingResult bindingResult,
            Principal principal
    ) {
        Answer answer = answerService.getAnswer(id);
        checkOwner(answer, principal);

        if (bindingResult.hasErrors()) {
            return "answer_form";
        }

        answerService.modify(answer, form.content());

        return String.format("redirect:/question/detail/%s", answer.getQuestion().getId());
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/delete/{id}")
    @Transactional
    public String answerDelete(@PathVariable int id, Principal principal) {
        Answer answer = answerService.getAnswer(id);
        checkOwner(answer, principal);

        int questionId = answer.getQuestion().getId();
        answerService.delete(answer);

        return String.format("redirect:/question/detail/%s", questionId);
    }

    // 교재 3-10: 답변 추천 (POST)
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/vote/{id}")
    public String answerVote(@PathVariable int id, Principal principal) {
        Answer answer = answerService.getAnswer(id);
        SiteUser siteUser = userService.getUser(principal.getName());
        answerService.vote(answer, siteUser);

        return String.format("redirect:/question/detail/%s", answer.getQuestion().getId());
    }

    private void checkOwner(Answer answer, Principal principal) {
        if (answer.getAuthor() == null || !answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "수정 또는 삭제 권한이 없습니다.");
        }
    }
}
