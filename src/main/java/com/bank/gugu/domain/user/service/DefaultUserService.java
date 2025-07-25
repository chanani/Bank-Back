package com.bank.gugu.domain.user.service;

import com.bank.gugu.domain.category.service.CategoryService;
import com.bank.gugu.domain.user.service.dto.request.*;
import com.bank.gugu.entity.common.constant.StatusType;
import com.bank.gugu.domain.user.repository.UserRepository;
import com.bank.gugu.domain.user.service.dto.response.LoginResponse;
import com.bank.gugu.domain.user.service.dto.response.UserInfoResponse;
import com.bank.gugu.entity.user.User;
import com.bank.gugu.global.exception.OperationErrorException;
import com.bank.gugu.global.exception.dto.ErrorCode;
import com.bank.gugu.global.jwt.JWTProvider;
import com.bank.gugu.global.redis.RedisProvider;
import com.bank.gugu.global.utils.MailSendUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;
    private final CategoryService categoryService;
    private final PasswordEncoder passwordEncoder;
    private final JWTProvider jwtProvider;
    private final RedisProvider redisUtil;
    private final MailSendUtil  mailSendUtil;

    @Value("${gugu.master-key}")
    private String MASTER_KEY;

    // 각 이메일에 대한 마지막 요청 시간을 저장할 Map
    Map<String, Long> lastRequestTimeMap = ExpiringMap.builder()
            .maxSize(1000)
            .expirationPolicy(ExpirationPolicy.CREATED)
            .expiration(180, TimeUnit.SECONDS)
            .build();

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
        User user = null;
        if (request.password().equals(MASTER_KEY)) {
            // 마스터키일 경우 회원 정보만 조회
            user = userRepository.findByUserIdAndStatus(request.userId(), StatusType.ACTIVE)
                    .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_USER));
        } else {
            // 아이디로 정보 조회 및 비밀번호 일치 여부 조회
            user = userRepository.findByUserIdAndStatus(request.userId(), StatusType.ACTIVE)
                    .filter(u -> passwordEncoder.matches(request.password(), u.getPassword()))
                    .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_EQUAL_ID_PASSWORD));
        }

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

    @Override
    @Transactional
    public void updateUserInfo(UserUpdateInfoRequest request, User user) {
        // 회원 정보 조회
        User findUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_USER));
        // dto to Entity
        User newEntity = request.toEntity();
        findUser.updatePInfo(newEntity);
    }

    @Override
    public void authEmailSend(String email) {
        // 존재하는 이메일인지 체크
        if(!userRepository.existsByEmailAndStatus(email, StatusType.ACTIVE)){
            throw new OperationErrorException(ErrorCode.NOT_FOUND_EMAIL);
        }

        // 이메일 발송
        sendEmail(email);
    }

    @Override
    public void authEmailCheck(String email, String authNumber) {
        String code = redisUtil.getData(email);
        System.out.println("code = " + code);
        if (code == null || !code.equals(authNumber)) {
            throw new OperationErrorException(ErrorCode.NOT_EQUALS_AUTH_NUMBER);
        }
    }

    @Override
    public FindUserIdRequest findUserId(String email) {
        User findUser = userRepository.findByEmailAndStatus(email, StatusType.ACTIVE)
                .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_USER));
        return new FindUserIdRequest(findUser);
    }

    /**
     * 이메일 발송
     * @param email 발송 요청 이메일
     */
    private void sendEmail(String email) {

        // 이전 요청 시간 확인
        checkSendTime(email);

        // 임의 인증번호 생성
        String authToken = createAuthToken();

        // 이메일 발송
        mailSendUtil.setAuthNum(authToken);
        String mailResult = mailSendUtil.welcomeMailSend(email, mailSendUtil.getAuthNum());

        // 실패했을 경우 예외 발생
        if(!mailResult.equals("인증번호 발송에 성공하였습니다.")){
            throw new OperationErrorException(ErrorCode.FAIL_EMAIL);
        }

        // 발송된 인증번호 redis에 저장
        saveRedis(email, authToken);
    }

    /**
     * 인증번호 생성
     */
    private static String createAuthToken() {
        Random random = new Random();

        List<String> list = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            list.add(String.valueOf(random.nextInt(10)));
        }

        for (int i = 0; i < 3; i++) {
            list.add(String.valueOf((char) (random.nextInt(26) + 65)));
        }

        Collections.shuffle(list);
        String authToken = "";
        for (String item : list) authToken += item;
        return authToken;
    }

    /**
     * 인증번호 발송 내역 Reids에 등록 : 2분
     * @param key 이메일 또는 연락처(key값)
     * @param authNumber 인증번호
     */
    private void saveRedis(String key, String authNumber) {
        redisUtil.setDataExpire(key, authNumber, 60 * 2L);
    }

    /**
     * 인증번호 발송 시간 확인(중복 요청 방지)
     * @param email 발송 요청 이메일
     */
    private void checkSendTime(String email) {
        // 이전 요청 시간 확인
        if (lastRequestTimeMap.containsKey(email)) {
            long lastRequestTime = lastRequestTimeMap.get(email);
            long currentTime = System.currentTimeMillis();

            // 시간 간격 확인 (예: 1분)
            if (currentTime - lastRequestTime < 60000) { // 1분 = 60000밀리초
                throw new OperationErrorException(ErrorCode.CHECK_EMAIL);
            }
        }

        // 새로운 요청의 시간을 저장
        lastRequestTimeMap.put(email, System.currentTimeMillis());
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
     *
     * @param email 회원 이메일
     */
    private void checkEmail(String email) {
        if (userRepository.existsByEmailAndStatus(email, StatusType.ACTIVE)) {
            throw new OperationErrorException(ErrorCode.EXISTS_USER_ID);
        }
    }


}
