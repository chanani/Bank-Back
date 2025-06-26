package com.bank.gugu.domain.record.repository.condition;

import com.bank.gugu.entity.user.User;
import com.bank.gugu.global.query.record.Range;

public record RecordMonthCondition(
        // 날짜
        Range range,

        // 회원 객체
        User user
) {
}
