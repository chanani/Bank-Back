package com.bank.gugu.controller.user;

import com.bank.gugu.domain.user.service.UserService;
import com.bank.gugu.domain.user.service.dto.request.JoinRequest;
import com.bank.gugu.domain.user.service.dto.request.LoginRequest;
import com.bank.gugu.domain.user.service.dto.request.UserUpdatePasswordRequest;
import com.bank.gugu.domain.user.service.dto.response.LoginResponse;
import com.bank.gugu.domain.user.service.dto.response.UserInfoResponse;
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
            description = "비밀번호 수정합니다.")
    @PutMapping("/api/v1/user/update-password")
    public ResponseEntity<ApiResponse> updateUserPassword(
            @Valid @RequestBody UserUpdatePasswordRequest request,
            @Parameter(hidden = true) User user
    ) {
        userService.updateUserPassword(request, user);
        return ResponseEntity.ok(ApiResponse.ok());
    }

}
