package com.bank.gugu.global.query.record;

/**
 * 생일 범위 데이터
 */
public record BirthRange(
        String start,
        String end
) {
    public static BirthRange create(String start, String end) {
        if (start == null || end == null) {
            return null;
        }
        return new BirthRange(start, end);
    }
}
