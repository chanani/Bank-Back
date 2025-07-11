package com.bank.gugu.domain.recordsImage.service;

import com.bank.gugu.domain.recordsImage.service.response.RecordsImagesResponse;
import com.bank.gugu.entity.user.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RecordsImageService {

    /**
     * 입/출금 내역 이미지 삭제
     * @param recordsImageId 이미지 식별자
     */
    void deleteRecordImage(Long recordsImageId);

    /**
     * 입/출금 내역 이미지 등록
     * @param recordsId 입/출금 식별자
     * @param inputFile 등록 요청 파일 객체
     * @param user 로그인 회원 식별자
     */
    void addRecordImage(Long recordsId, MultipartFile inputFile, User user);

    /**
     * 입/출금 이미지 월별 목록 조회
     * @param date 년/월 (yyyy-mm)
     * @param user 로그인 회원 객체
     * @return 입/출금 이미지 월별 목록
     */
    List<RecordsImagesResponse> getRecordsImages(String date, User user);
}
