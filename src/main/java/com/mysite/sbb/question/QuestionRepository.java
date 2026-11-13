package com.mysite.sbb.question;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
    Optional<Question> findBySubject(String subject);

    Optional<Question> findBySubjectAndContent(String subject, String content);

    List<Question> findBySubjectLike(String subjectLike);

    // JpaRepository 가 이미 제공하지만, 페이징 조회를 쓴다는 것을 드러내기 위해 교재처럼 선언한다. (교재 3-02)
    Page<Question> findAll(Pageable pageable);

    // 교재 3-13: 제목·내용·질문 글쓴이·답변 내용·답변 글쓴이에서 키워드를 찾는다.
    // 답변이 여러 개 걸리면 같은 질문이 반복되므로 distinct 가 필요하다.
    @Query("select distinct q "
            + "from Question q "
            + "left outer join SiteUser u1 on q.author = u1 "
            + "left outer join Answer a on a.question = q "
            + "left outer join SiteUser u2 on a.author = u2 "
            + "where q.subject like %:kw% "
            + "or q.content like %:kw% "
            + "or u1.username like %:kw% "
            + "or a.content like %:kw% "
            + "or u2.username like %:kw%")
    Page<Question> findAllByKeyword(@Param("kw") String kw, Pageable pageable);
}