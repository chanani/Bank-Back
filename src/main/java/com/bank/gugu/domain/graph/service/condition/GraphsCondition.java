package com.bank.gugu.domain.graph.service.condition;

import com.bank.gugu.entity.common.constant.RecordType;
import com.bank.gugu.entity.user.User;
import com.bank.gugu.global.query.record.Range;

public record GraphsCondition(
        // 날짜
        Range range,
        // 입/출금 타입
        RecordType type,
        // 회원 객체
        User user
){

}
