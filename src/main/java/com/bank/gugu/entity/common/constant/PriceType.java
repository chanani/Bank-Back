package com.bank.gugu.entity.common.constant;

/**
 * 결제 수단 타입
 */
public enum PriceType {
    CARD("신용카드"),
    CHECK_CARD("체크카드"),
    CASH("현금"),
    BANK("계좌이체");

    private final String value;

    PriceType(String value) {
        this.value = value;
    }
}
