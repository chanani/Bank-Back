package com.bank.gugu.domain.record.repository;

import com.bank.gugu.domain.record.repository.condition.RecordCurrentCondition;
import com.bank.gugu.domain.record.repository.condition.RecordMonthCondition;
import com.bank.gugu.domain.record.service.dto.response.RecordsCurrentResponse;
import com.bank.gugu.domain.record.service.dto.response.RecordsMonthResponse;

import java.util.List;

public interface RecordsRepositoryCustom {

    /**
     * 입/출금 내역 조회(하루)
     * @param condition 검색 객체
     * @return 입/출금 내역
     */
    List<RecordsCurrentResponse> findCurrentQuery(RecordCurrentCondition condition);

    /**
     * 입/출금 내역 조회(한달)
     * @param condition 검색 객체
     * @return 입/출금 내역
     */
    List<RecordsMonthResponse> findMonthQuery(RecordMonthCondition condition);
}
