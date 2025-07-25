package com.bank.gugu.domain.user.service.dto.request;

import com.bank.gugu.domain.user.service.constant.FindType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FindAuthSendRequest(
        @Schema(description = "타입", example = "ID")
        @NotNull(message = "타입은 필수입니다.")
        FindType type,

        @Schema(description = "아이디", example = "chanhan12")
        String userId,

        @Schema(description = "이메일", example = "chanhan12@naver.com")
        @NotBlank(message = "이메일은 필수입니다.")
        String email
) {
}
