package com.bank.gugu.entity.common.constant;

/**
 * 회원, 관리자 등 타입
 */
public enum UserType {
    USER("ROLE_USER"),
    OPERATOR("ROLE_ADMIN");

    private final String role;

    UserType(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
