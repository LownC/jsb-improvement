package com.mysite.sbb.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 회원 엔티티. (교재 3-06)
 * 클래스 이름을 User 로 하면 Spring Security 의 User 클래스와 헷갈리므로 SiteUser 로 한다.
 */
@Entity
@Getter
@Setter
public class SiteUser {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private int id;

    @Column(unique = true)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;
}
