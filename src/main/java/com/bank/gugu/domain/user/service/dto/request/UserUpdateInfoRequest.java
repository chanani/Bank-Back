package com.bank.gugu.domain.user.service.dto.request;

import com.bank.gugu.entity.user.User;
import com.bank.gugu.global.regex.Regex;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public record UserUpdateInfoRequest(
        @Schema(description = "이메일", example = "gg@gg.gg")
        @Pattern(regexp = Regex.EMAIL, message = "이메일 형식을 확인해주세요.")
        @NotEmpty(message = "이메일은 필수입니다.")
        String email
) {
    public User toEntity() {
        return User.builder()
                .email(this.email)
                .build();
    }
}
