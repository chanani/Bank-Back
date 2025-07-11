package com.bank.gugu.domain.recordsImage.repository.condition;

import com.bank.gugu.entity.user.User;
import com.bank.gugu.global.query.record.Range;

public record RecordImagesMonthCondition(
        // 날짜
        Range range,

        // 회원 객체
        User user
) {
}
