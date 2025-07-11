package com.bank.gugu.domain.recordsImage.repository;

import com.bank.gugu.domain.recordsImage.repository.condition.RecordImagesMonthCondition;
import com.bank.gugu.domain.recordsImage.service.response.RecordsImagesMonthResponse;

import java.util.List;

public interface RecordsImageRepositoryCustom {

    /**
     * 입/출금 이미지 목록 조회
     * @param condition 검색 객체
     * @return 입/출금 이미지 목록 & 연도
     */
    List<RecordsImagesMonthResponse> findMonthQuery(RecordImagesMonthCondition condition);
}
