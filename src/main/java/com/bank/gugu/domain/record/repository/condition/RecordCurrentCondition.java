package com.bank.gugu.domain.record.repository.condition;

import com.bank.gugu.entity.user.User;

import java.time.LocalDate;

public record RecordCurrentCondition(
        // 날짜
        LocalDate currentDate,

        // 회원 객체
        User user
) {
}
