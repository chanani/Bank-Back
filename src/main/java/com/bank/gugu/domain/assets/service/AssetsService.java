package com.bank.gugu.domain.assets.service;

import com.bank.gugu.domain.assets.service.request.AssetsCreateRequest;
import com.bank.gugu.domain.assets.service.request.AssetsUpdateRequest;
import com.bank.gugu.domain.assets.service.response.AssetsPageResponse;
import com.bank.gugu.domain.assets.service.response.AssetsResponse;
import com.bank.gugu.entity.user.User;

public interface AssetsService {

    /**
     * 자산 그룹 생성
     * @param request 생성 요청 객체
     * @param user 로그인 회원 객체
     */
    void addAssets(AssetsCreateRequest request, User user);

    /**
     * 자산 그룹 수정
     * @param request 수정 요청 객체
     * @param assetsId 자산 식별자
     */
    void updateAssets(AssetsUpdateRequest request, Long assetsId);

    /**
     * 자산 그룹 삭제
     * @param assetsId 자산 식별자
     */
    void deleteAssets(Long assetsId);

    /**
     * 자산 목록 및 총 자산 조회
     * @param user 로그인 회원 객체
     * @return 자산 리스트 및 총 자산
     */
    AssetsPageResponse getAssetsList(User user);

    /**
     * 자산 정보 상세 조회
     * @param assetsId 자산 식별자
     * @return 자상 상세 정보
     */
    AssetsResponse getAssets(Long assetsId);
}
