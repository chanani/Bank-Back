package com.bank.gugu.domain.record.service;

import com.bank.gugu.domain.record.service.dto.request.RecordCreateRequest;
import com.bank.gugu.entity.user.User;

public interface RecordsService {

    /**
     * 입/출금 내역 등록
     * @param request 등록 요청 객체
     * @param user 로그인 회원 객체
     */
    void addRecord(RecordCreateRequest request, User user);

    /**
     * 입/출금 내역 삭제
     * @param recordsId 내역 식별자
     */
    void deleteRecord(Long recordsId);
}
