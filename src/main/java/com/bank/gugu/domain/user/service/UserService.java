package com.bank.gugu.domain.user.service;

import com.bank.gugu.domain.user.service.dto.request.*;
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


    /**
     * 회원 정보 수정
     *
     * @param request 수정 요청 객체
     * @param user    로그인 회원 객체
     */
    void updateUserInfo(UserUpdateInfoRequest request, User user);

    /**
     * 인증번호 발송
     * @param email 발송 요청 이메일
     */
    void authEmailSend(String email);

    /**
     * 발송된 인증번호 일치 여부 확인
     * @param email 발송 이메일
     * @param authNumber 확인 인증번호
     */
    void authEmailCheck(String email, String authNumber);

    /**
     * 회원 아이디 조회(이메일로 조회)
     * @param email 조회 요청 이메일
     * @return 마스킹된 회원 아이디
     */
    FindUserIdRequest findUserId(String email);
}
