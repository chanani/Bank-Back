package com.bank.gugu.controller.user;

import com.bank.gugu.domain.user.repository.UserRepository;
import com.bank.gugu.domain.user.service.UserService;
import com.bank.gugu.domain.user.service.dto.request.*;
import com.bank.gugu.domain.user.service.dto.response.LoginResponse;
import com.bank.gugu.domain.user.service.dto.response.UserInfoResponse;
import com.bank.gugu.entity.common.constant.StatusType;
import com.bank.gugu.entity.user.User;
import com.bank.gugu.global.annotation.NoneAuth;
import com.bank.gugu.global.response.ApiResponse;
import com.bank.gugu.global.response.DataResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User API Controller", description = "회원 관련 API를 제공합니다.")
@RestController
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;
    private final UserRepository userRepository;

    @Operation(summary = "회원가입 API",
            description = "회원가입을 진행합니다.")
    @NoneAuth
    @PostMapping("/api/v1/none/join")
    public ResponseEntity<ApiResponse> join(@Valid @RequestBody JoinRequest request) {
        userService.join(request);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @Operation(summary = "로그인 API",
            description = "로그인을 진행합니다.")
    @NoneAuth
    @PostMapping("/api/v1/none/login")
    public ResponseEntity<DataResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) throws Exception {
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(DataResponse.send(response));
    }

    @Operation(summary = "회원 정보 조회 API",
            description = "회원 정보를 조회합니다.")
    @GetMapping("/api/v1/user/info")
    public ResponseEntity<DataResponse<UserInfoResponse>> getUserInfo(@Parameter(hidden = true) User user) {
        UserInfoResponse userInfo = userService.getInfo(user);
        return ResponseEntity.ok(DataResponse.send(userInfo));
    }

    @Operation(summary = "비밀번호 수정 API",
            description = "비밀번호를 수정합니다.")
    @PutMapping("/api/v1/user/update-password")
    public ResponseEntity<ApiResponse> updateUserPassword(
            @Valid @RequestBody UserUpdatePasswordRequest request,
            @Parameter(hidden = true) User user
    ) {
        userService.updateUserPassword(request, user);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @Operation(summary = "회원정보 수정 API",
            description = "회원정보를 수정합니다.")
    @PutMapping("/api/v1/user/info")
    public ResponseEntity<ApiResponse> updateUserInfo(
            @Valid @RequestBody UserUpdateInfoRequest request,
            @Parameter(hidden = true) User user
    ) {
        userService.updateUserInfo(request, user);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @Operation(summary = "아이디 중복 검사 API",
            description = "아이디 사용 가능 여부를 확인합니다.")
    @NoneAuth
    @GetMapping("/api/v1/none/check/id")
    public ResponseEntity<DataResponse<Boolean>> checkUserId(@Parameter(name = "userId") String userId) {
        boolean checkUserId = userRepository.existsByUserId(userId);
        return ResponseEntity.ok(DataResponse.send(checkUserId));
    }

    @Operation(summary = "이메일 중복 검사 API",
            description = "이메일 사용 가능 여부를 확인합니다.")
    @NoneAuth
    @GetMapping("/api/v1/none/check/email")
    public ResponseEntity<DataResponse<Boolean>> checkEmail(@Parameter(name = "email") String email) {
        boolean checkEmail = userRepository.existsByEmailAndStatus(email, StatusType.ACTIVE);
        return ResponseEntity.ok(DataResponse.send(checkEmail));
    }

    @Operation(summary = "인증번호 발송 API",
            description = "인증번호를 발송합니다.")
    @NoneAuth
    @PostMapping("/api/v1/none/email-send")
    public ResponseEntity<ApiResponse> authEmailSend(@Valid @RequestBody FindAuthSendRequest request) {

        // 인증번호 발송 및 Redis에 인증번호 저장
        userService.authEmailSend(request);

        return ResponseEntity.ok(ApiResponse.ok());
    }

    @Operation(summary = "인증번호 확인 API",
            description = "인증번호를 확인합니다.")
    @NoneAuth
    @GetMapping("/api/v1/none/email-check")
    public ResponseEntity<ApiResponse> authEmailCheck(
            @RequestParam(name = "email") String email,
            @RequestParam(name = "authNumber") String authNumber) {

        // 인증번호 일치 여부 확인
        userService.authEmailCheck(email, authNumber);

        return ResponseEntity.ok(ApiResponse.ok());
    }

    @Operation(summary = "회원 아이디를 조회합니다. API",
            description = """
                    이메일을 통해 회원 아이디를 조회합니다.
                    아이디의 마지막 3글자는 '*'로 처리됩니다.
                    """)
    @NoneAuth
    @GetMapping("/api/v1/none/find-id")
    public ResponseEntity<DataResponse<FindUserIdRequest>> getUserId(
            @RequestParam(name = "email") String email) {

        // 회원 아이디 조회
        FindUserIdRequest response = userService.findUserId(email);

        return ResponseEntity.ok(DataResponse.send(response));
    }

    @Operation(summary = "비밀번호 재등록 API",
            description = "비밀번호를 재등록합니다.")
    @NoneAuth
    @PutMapping("/api/v1/user/update-find-password")
    public ResponseEntity<ApiResponse> updateUserPassword(
            @Valid @RequestBody UserUpdateFindPasswordRequest request
    ) {
        userService.updateFindUserPassword(request);
        return ResponseEntity.ok(ApiResponse.ok());
    }
}
