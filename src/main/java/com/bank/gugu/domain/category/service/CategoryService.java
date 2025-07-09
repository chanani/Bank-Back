package com.bank.gugu.domain.category.service;

import com.bank.gugu.domain.category.service.dto.request.CategoryCreateRequest;
import com.bank.gugu.domain.category.service.dto.request.CategoryUpdateOrderRequest;
import com.bank.gugu.domain.category.service.dto.request.CategoryUpdateRequest;
import com.bank.gugu.domain.category.service.dto.response.CategoriesResponse;
import com.bank.gugu.domain.category.service.dto.response.CategoryResponse;
import com.bank.gugu.entity.common.constant.RecordType;
import com.bank.gugu.entity.user.User;
import com.bank.gugu.global.page.Page;
import jakarta.validation.Valid;

import java.util.List;

public interface CategoryService {

    /**
     * 회원 가입 시 기본 카테고리 생성
     *
     * @param user 로그인 회원 객체
     */
    void addCategories(User user);

    /**
     * 카테고리 등록
     *
     * @param request 등록 요청 객체
     * @param user    로그인 회원 객체
     */
    void addCategory(CategoryCreateRequest request, User user);

    /**
     * 카테고리 수정
     * @param categoryId 카테고리 식별자
     * @param request 수정 요청 객체
     * @param user 로그인 회원 객체
     */
    void updateCategory(Long categoryId, CategoryUpdateRequest request, User user);


    /**
     * 카테고리 삭제
     * @param categoryId 카테고리 식별자
     */
    void deleteCategory(Long categoryId);

    /**
     * 카테고리 목록 조회
     * @param user 로그인 회원 객체
     * @param type 지출/수입 타입
     * @return 회원의 카테고리 리스트
     */
    List<CategoriesResponse> getCategories(User user, RecordType type);

    /**
     * 카테고리 상세정보 조회
     * @param categoryId 카테고리 식별자
     * @return 카테고리 상세 정보
     */
    CategoryResponse getCategory(Long categoryId);

    /**
     * 카테고리 순서 변경
     * @param request 변경 요청 객체
     * @param user 로그인 회원 객체
     */
    void updateOrder(@Valid CategoryUpdateOrderRequest request, User user);


}
