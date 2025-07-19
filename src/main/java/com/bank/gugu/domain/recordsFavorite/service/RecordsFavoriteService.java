package com.bank.gugu.domain.recordsFavorite.service;

import com.bank.gugu.domain.recordsFavorite.service.dto.request.RecordsFavoriteCreateRequest;
import com.bank.gugu.entity.user.User;
import jakarta.validation.Valid;

public interface RecordsFavoriteService {
    /**
     * 입/출금 내역 즐겨찾기 생성
     * @param request 생성 요청 객체
     * @param user 로그인 회원 객체
     */
    void addRecordsFavorite(RecordsFavoriteCreateRequest request, User user);

    /**
     * 입/출금 내역 즐겨찾기 삭제
     * @param recordsFavoriteId 입/출금 내역 즐겨찾기 식별자
     */
    void deleteRecordsFavorite(Long recordsFavoriteId);
}
