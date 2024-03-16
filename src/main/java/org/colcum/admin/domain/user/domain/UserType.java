package org.colcum.admin.domain.user.domain;

import lombok.Getter;

@Getter
public enum UserType {
    MEMBER("ROLE_MEMBER"), LEADER("ROLE_LEADER"), STAFF("ROLE_STAFF");

    private final String role;

    UserType(String role) {
        this.role = role;
    }
}
