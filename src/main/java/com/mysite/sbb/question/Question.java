package com.mysite.sbb.question;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Setter
@Getter
public class Question {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private int id;

    private LocalDateTime createDate;

    // 교재 3-09: 수정한 적이 있을 때만 값이 들어간다.
    private LocalDateTime modifyDate;

    @Column(length = 200)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    // orphanRemoval: answers 목록에서 빠진 답변은 DB에서도 지워진다. (강사 14168 37강)
    @OneToMany(mappedBy = "question", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Answer> answers = new ArrayList<>();

    // 교재 3-08: 글쓴이. 여러 질문이 한 사용자를 가리키므로 @ManyToOne. 강사 권고대로 LAZY.
    @ManyToOne(fetch = LAZY)
    private SiteUser author;

    public Answer addAnswer(String content, SiteUser author) {
        Answer answer = new Answer();
        answer.setContent(content);
        answer.setQuestion(this);
        answer.setAuthor(author);
        answer.setCreateDate(LocalDateTime.now());
        answers.add(answer);

        return answer;
    }

    // 목록에서 제거하면 orphanRemoval 에 의해 트랜잭션 종료 시 DELETE 가 실행된다.
    public boolean removeAnswer(Answer answer) {
        return answers.remove(answer);
    }
}
