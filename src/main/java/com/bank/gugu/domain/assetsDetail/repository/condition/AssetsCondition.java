package com.bank.gugu.domain.assetsDetail.repository.condition;

import com.bank.gugu.entity.common.constant.RecordType;
import com.bank.gugu.global.query.record.Range;

public record AssetsCondition (
        // 계좌 식별자
        Long assetsId,

        // 키워드
        String keyword,

        // 거래 유형
        RecordType type,

        // 정렬 차순
        String sort,

        // 조회 날짜
        Range range
){
}
