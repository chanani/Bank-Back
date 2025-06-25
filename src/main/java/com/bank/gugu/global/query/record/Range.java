package com.bank.gugu.global.query.record;

import java.time.LocalDate;

/**
 * LocalDate 범위 데이터
 * */
public record Range(
        LocalDate start,
        LocalDate end
) {

    public static Range create(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            return null;
        }
        return new Range(start, end);

    }

    public static Range defaultRange() {
        LocalDate now = LocalDate.now();
        return new Range(now, now);
    }

}
