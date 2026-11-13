package com.mysite.sbb.question;

import com.mysite.sbb.answer.AnswerForm;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/question")
public class QuestionController {
    private final QuestionService questionService;
    private final UserService userService;

    // ?page=N (0부터 시작) 파라미터로 페이지를 고른다. 없으면 첫 페이지.
    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
        Page<Question> paging = questionService.getList(page);
        model.addAttribute("paging", paging);

        return "question_list";
    }

    // AnswerForm 매개변수는 상세 화면의 답변 폼(th:object="${answerForm}")을 위해 빈 객체를 모델에 싣는 역할만 한다.
    @GetMapping("/detail/{id}")
    public String detail(Model model, @PathVariable int id, @ModelAttribute("answerForm") AnswerForm answerForm) {
        Question question = questionService.getQuestion(id);

        model.addAttribute("question", question);
        return "question_detail";
    }

    // 교재 3-08: 로그인한 사용자만 질문을 등록할 수 있다. 비로그인이면 시큐리티가 로그인 페이지로 보낸다.
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String questionCreate(@ModelAttribute("form") QuestionForm form) {
        return "question_form";
    }

    // @Valid 로 검증하고, 실패하면 예외 대신 BindingResult 에 담겨 폼을 다시 보여줄 수 있다. (강사 14168 13강)
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String questionCreate(
            @ModelAttribute("form") @Valid QuestionForm form,
            BindingResult bindingResult,
            Principal principal
    ) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }

        // Principal 에는 로그인한 사용자명만 있으므로 엔티티는 DB에서 다시 조회한다.
        SiteUser author = userService.getUser(principal.getName());
        questionService.create(form.subject(), form.content(), author);

        return "redirect:/question/list";
    }

    // 교재 3-09: 수정 폼. 기존 값을 폼에 채워서 보여준다. 글쓴이가 아니면 403.
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String questionModify(Model model, @PathVariable int id, Principal principal) {
        Question question = questionService.getQuestion(id);
        checkOwner(question, principal);

        model.addAttribute("form", new QuestionForm(question.getSubject(), question.getContent()));
        model.addAttribute("modifying", true);
        return "question_form";
    }

    // 강사 14168 41강: 수정은 PUT. 폼은 _method=put 숨은 필드로 전송하고 HiddenHttpMethodFilter 가 PUT 으로 바꿔 준다.
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/modify/{id}")
    public String questionModify(
            Model model,
            @PathVariable int id,
            @ModelAttribute("form") @Valid QuestionForm form,
            BindingResult bindingResult,
            Principal principal
    ) {
        Question question = questionService.getQuestion(id);
        checkOwner(question, principal);

        if (bindingResult.hasErrors()) {
            model.addAttribute("modifying", true);
            return "question_form";
        }

        questionService.modify(question, form.subject(), form.content());

        return String.format("redirect:/question/detail/%s", id);
    }

    // 강사 14168 41강: 삭제는 DELETE. 화면의 a[method="DELETE"] 링크를 global.js 가 폼 전송으로 바꿔 준다.
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/delete/{id}")
    public String questionDelete(@PathVariable int id, Principal principal) {
        Question question = questionService.getQuestion(id);
        checkOwner(question, principal);

        questionService.delete(question);

        return "redirect:/";
    }

    // 글쓴이 본인인지 확인. 교재는 400(BAD_REQUEST)을 쓰지만 권한 문제이므로 403(FORBIDDEN)이 더 정확하다.
    private void checkOwner(Question question, Principal principal) {
        if (question.getAuthor() == null || !question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "수정 또는 삭제 권한이 없습니다.");
        }
    }
}
