package com.bank.gugu.entity.common.constant;

import lombok.Getter;

/**
 * 입/출금 타입
 */
@Getter
public enum RecordType {
    ALL("전체"),
    DEPOSIT("수입"),
    WITHDRAW("지출");

    private final String value;

    RecordType(String value) {
        this.value = value;
    }
}
