package com.bank.gugu.domain.user.service;

import com.bank.gugu.domain.user.service.dto.request.JoinRequest;
import com.bank.gugu.domain.user.service.dto.request.LoginRequest;
import com.bank.gugu.domain.user.service.dto.request.UserUpdatePasswordRequest;
import com.bank.gugu.domain.user.service.dto.response.LoginResponse;
import com.bank.gugu.domain.user.service.dto.response.UserInfoResponse;
import com.bank.gugu.entity.user.User;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    /**
     * 회원가입
     *
     * @param request 회원가입 요청 객체
     */
    void join(JoinRequest request);

    /**
     * 사용자 로그인
     *
     * @param request 로그인 요청 객체
     * @return accessToken, refreshToken 객체 반환
     */
    LoginResponse login(LoginRequest request) throws Exception;

    /**
     * 회원 비밀번호 수정
     *
     * @param request 비밀번호 수정 요청 객체
     * @param user    로그인 회원 객체
     */
    void updateUserPassword(UserUpdatePasswordRequest request, User user);

    /**
     * 회원 정보 조회
     *
     * @param user 로그인 사용자 객체
     * @return 로그인 회원 객체
     */
    UserInfoResponse getInfo(User user);


}
