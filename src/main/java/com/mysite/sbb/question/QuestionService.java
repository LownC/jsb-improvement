package com.mysite.sbb.question;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;

    public List<Question> getList() {
        return questionRepository.findAll();
    }

    // 한 페이지 10건, 최신 글이 먼저 오도록 작성일시 내림차순 정렬 (교재 3-02)
    // 작성일시가 같은 글(샘플 데이터 등)의 순서가 매번 바뀌지 않도록 id 내림차순을 2차 정렬로 둔다.
    public Page<Question> getList(int page) {
        Pageable pageable = PageRequest.of(page, 10,
                Sort.by(Sort.Order.desc("createDate"), Sort.Order.desc("id")));
        return questionRepository.findAll(pageable);
    }

    public Question getQuestion(int id) {
        Optional<Question> opQuestion = questionRepository.findById(id);

        if (opQuestion.isPresent()) {
            return opQuestion.get();
        } else {
            throw new DataNotFoundException("question not found");
        }
    }

    public Question create(String subject, String content, SiteUser author) {
        Question q = new Question();
        q.setSubject(subject);
        q.setContent(content);
        q.setCreateDate(LocalDateTime.now());
        q.setAuthor(author);

        return questionRepository.save(q);
    }
}
