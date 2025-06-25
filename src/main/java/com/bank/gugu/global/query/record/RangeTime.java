package com.bank.gugu.global.query.record;

import java.time.LocalDateTime;

/**
 * LocalDateTime 범위 데이터
 * */
public record RangeTime(
        LocalDateTime start,
        LocalDateTime end
) {
}
