package com.bank.gugu.entity.common.constant;

/**
 * 상태 유형
 */
public enum StatusType {
    ACTIVE("활성화"),
    INACTIVE("비활성화"),
    DELETED("삭제");

    private final String value;

    StatusType(String value) {
        this.value = value;
    }
}
