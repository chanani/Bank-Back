package com.bank.gugu.domain.record.service;

import com.bank.gugu.domain.record.service.dto.request.RecordCreateRequest;
import com.bank.gugu.domain.record.service.dto.request.RecordUpdateRequest;
import com.bank.gugu.domain.record.service.dto.response.RecordsCurrentResponse;
import com.bank.gugu.domain.record.service.dto.response.RecordsResponse;
import com.bank.gugu.entity.user.User;

import java.time.LocalDate;
import java.util.List;

public interface RecordsService {

    /**
     * 입/출금 내역 등록
     *
     * @param request 등록 요청 객체
     * @param user    로그인 회원 객체
     */
    void addRecord(RecordCreateRequest request, User user);

    /**
     * 입/출금 내역 삭제
     *
     * @param recordsId 내역 식별자
     */
    void deleteRecord(Long recordsId);

    /**
     * 입/출금 내역 수정
     *
     * @param request   수정 요청 객체
     * @param recordsId 내역 식별자
     */
    void updateRecord(RecordUpdateRequest request, Long recordsId);

    /**
     * 입/출금 내역 조회(하루)
     *
     * @param currentDate 조회 날짜
     * @param user        로그인 회원 객체
     * @return 입/출금 내역
     */
    List<RecordsCurrentResponse> getCurrentRecord(LocalDate currentDate, User user);

    /**
     * 입/출금 내역 조회(한달)
     * @param date 조회 날짜
     * @param user 로그인 회원 객체
     * @return 입/출금 내역
     */
    List<RecordsResponse> getMonthRecord(String date, User user);
}
