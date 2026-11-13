package com.mysite.sbb.answer;

import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Setter
@Getter
public class Answer {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private int id;

    private LocalDateTime createDate;

    // 교재 3-09: 수정한 적이 있을 때만 값이 들어간다.
    private LocalDateTime modifyDate;

    @ManyToOne(fetch = LAZY)
    private Question question;

    @Column(columnDefinition = "TEXT")
    private String content;

    // 교재 3-08: 답변 글쓴이
    @ManyToOne(fetch = LAZY)
    private SiteUser author;
}
