package com.mysite.sbb.user;

import lombok.Getter;

/**
 * 권한 상수. Spring Security 는 "ROLE_" 접두사가 붙은 권한 이름을 역할(role)로 취급한다. (교재 3-07)
 */
@Getter
public enum UserRole {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }
}
