package com.bank.gugu.domain.user.service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @Schema(description = "아이디", example = "chanhan12")
        @NotBlank(message = "아이디는 필수입니다.")
        String userId,

        @Schema(description = "비밀번호", example = "zxc123!@#")
        @NotBlank(message = "비밀번호는 필수입니다.")
        String password,

        @Schema(description = "자동 로그인", example = "true")
        @NotNull(message = "자동 로그인 값은 필수입니다.")
        boolean isAutoLogin
) {
}
