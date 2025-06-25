package com.bank.gugu.global.query.record;

import java.math.BigDecimal;

/**
 * 금액 범위 데이터
 * */
public record AmountRange(
        BigDecimal start,
        BigDecimal end
) {
    public static AmountRange create(BigDecimal start, BigDecimal end) {
        if (start == null || end == null) {
            return null;
        }
        return new AmountRange(start, end);

    }
}
