package com.bank.gugu.domain.assetsDetail.service;

import com.bank.gugu.controller.assetsDetail.input.AssetsDetailsInput;
import com.bank.gugu.domain.assetsDetail.service.request.AssetsDetailCreateRequest;
import com.bank.gugu.domain.assetsDetail.service.response.AssetsDetailResponse;
import com.bank.gugu.entity.user.User;
import com.bank.gugu.global.page.Page;
import com.bank.gugu.global.page.PageInput;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface AssetsDetailService {

    /**
     * 자산 상세정보 등록
     * @param request 등록 요청 객체
     * @param user 로그인 회원 객체
     */
    void addAssetsDetail(AssetsDetailCreateRequest request, User user);

    /**
     * 자산 상세정보 삭제
     * @param assetsDetailId 자산 상세정보 식별자
     */
    void deleteAssetsDetail(Long assetsDetailId);

    /**
     * 자산 상세정보 목록 조회
     * @param pageInput 페이징 정보 객체
     * @param input 검색 객체
     * @param user 로그인 회원 객체
     * @return 무한스크롤된 자산 상세 정보
     */
    Slice<AssetsDetailResponse> getAssetsDetails(PageInput pageInput, AssetsDetailsInput input, User user);
}
