package com.bank.gugu.domain.recordsImage.service;

import com.bank.gugu.entity.user.User;
import org.springframework.web.multipart.MultipartFile;

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
}
