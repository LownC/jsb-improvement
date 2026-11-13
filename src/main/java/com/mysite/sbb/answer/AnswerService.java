package com.mysite.sbb.answer;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 답변 조회·수정·삭제. (교재 3-09)
 * 답변 등록은 Question.addAnswer() + cascade 로 처리하므로 여기에는 없다.
 */
@Service
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;

    public Answer getAnswer(int id) {
        return answerRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("answer not found"));
    }

    @Transactional
    public void modify(Answer answer, String content) {
        answer.setContent(content);
        answer.setModifyDate(LocalDateTime.now());
        answerRepository.save(answer);
    }

    // 질문의 answers 목록에서 빼고(orphanRemoval) 명시적으로도 삭제한다.
    // 목록에서 빼지 않으면 cascade PERSIST 때문에 삭제가 취소될 수 있다.
    @Transactional
    public void delete(Answer answer) {
        answer.getQuestion().removeAnswer(answer);
        answerRepository.delete(answer);
    }

    // 교재 3-10: 추천
    @Transactional
    public void vote(Answer answer, SiteUser siteUser) {
        answer.getVoter().add(siteUser);
        answerRepository.save(answer);
    }
}
