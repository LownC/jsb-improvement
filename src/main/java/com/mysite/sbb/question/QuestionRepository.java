package com.mysite.sbb.question;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
    Optional<Question> findBySubject(String subject);

    Optional<Question> findBySubjectAndContent(String subject, String content);

    List<Question> findBySubjectLike(String subjectLike);

    // JpaRepository 가 이미 제공하지만, 페이징 조회를 쓴다는 것을 드러내기 위해 교재처럼 선언한다. (교재 3-02)
    Page<Question> findAll(Pageable pageable);
}