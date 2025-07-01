package com.bank.gugu.domain.user.service;

import com.bank.gugu.domain.category.repository.CategoryRepository;
import com.bank.gugu.domain.category.service.CategoryService;
import com.bank.gugu.entity.category.Category;
import com.bank.gugu.entity.common.constant.StatusType;
import com.bank.gugu.domain.user.repository.UserRepository;
import com.bank.gugu.domain.user.service.dto.request.JoinRequest;
import com.bank.gugu.domain.user.service.dto.request.LoginRequest;
import com.bank.gugu.domain.user.service.dto.request.UserUpdatePasswordRequest;
import com.bank.gugu.domain.user.service.dto.response.LoginResponse;
import com.bank.gugu.domain.user.service.dto.response.UserInfoResponse;
import com.bank.gugu.entity.user.User;
import com.bank.gugu.global.exception.OperationErrorException;
import com.bank.gugu.global.exception.dto.ErrorCode;
import com.bank.gugu.global.jwt.JWTProvider;
import com.bank.gugu.global.regex.Regex;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;
    private final CategoryService categoryService;
    private final PasswordEncoder passwordEncoder;
    private final JWTProvider jwtProvider;

    @Override
    @Transactional
    public void join(JoinRequest request) {
        // 아이디 중복 검사
        checkUserId(request.userId());
        // 비밀번호 일치 여부 확인
        equalPassword(request.password(), request.passwordCheck());
        // 이메일 중복 검사
        checkEmail(request.email());
        // Entity로 변환
        User newUser = request.toEntity();
        // 회원가입
        User user = userRepository.save(newUser);
        log.info("join success ! user id = {}", user.getUserId());

        // 기본 카테고리 생성
        categoryService.addCategories(user);
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) throws Exception {
        // 아이디로 정보 조회 및 비밀번호 일치 여부 조회
        User user = userRepository.findByUserIdAndStatus(request.userId(), StatusType.ACTIVE)
                .filter(u -> passwordEncoder.matches(request.password(), u.getPassword()))
                .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_EQUAL_ID_PASSWORD));

        // accessToken 발급
        String accessToken = jwtProvider.createAccessToken(user.getId());
        // refreshToken 발급
        String refreshToken = jwtProvider.createRefreshToken(user.getId());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }


    @Override
    @Transactional
    public void updateUserPassword(UserUpdatePasswordRequest request, User user) {
        // 비밀번호 일치 여부 확인
        equalPassword(request.password(), request.passwordCheck());
        // 회원 정보 조회
        User findUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_USER));
        // 비밀번호 변경
        findUser.updatePassword(request.password());
    }

    @Override
    public UserInfoResponse getInfo(User user) {
        User findUser = userRepository.findByIdAndStatus(user.getId(), StatusType.ACTIVE)
                .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_USER));
        return new UserInfoResponse(findUser);
    }

    /**
     * 회원 아이디 중복 체크(탈퇴한 아이디로 가입 불가)
     * 중복일 경우 바로 예외 발생
     *
     * @param userId 회원 아이디
     */
    private void checkUserId(String userId) {
        if (userRepository.existsByUserId(userId)) {
            throw new OperationErrorException(ErrorCode.EXISTS_USER_ID);
        }
    }

    /**
     * 비밀번호 동일 여부 체크
     * 동일하지 않을 경우 예외 발생
     *
     * @param password      비밀번호
     * @param passwordCheck 확인 비밀번호
     */
    private void equalPassword(String password, String passwordCheck) {
        if (!password.equals(passwordCheck)) {
            throw new OperationErrorException(ErrorCode.NOT_EQUAL_PASSWORD);
        }
    }

    /**
     * 회원 이메일 중복 체크
     * @param email 회원 이메일
     */
    private void checkEmail(String email) {
        if (userRepository.existsByUserEmail(email)) {
            throw new OperationErrorException(ErrorCode.EXISTS_USER_ID);
        }
    }


}
