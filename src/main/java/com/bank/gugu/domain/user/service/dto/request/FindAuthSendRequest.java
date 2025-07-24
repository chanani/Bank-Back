package com.bank.gugu.domain.user.service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class FindAuthSendRequest {

    @Schema(description = "이메일", example = "chanhan12@naver.com")
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;
}
