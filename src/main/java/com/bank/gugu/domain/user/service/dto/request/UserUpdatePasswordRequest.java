package com.bank.gugu.domain.user.service.dto.request;

import com.bank.gugu.global.regex.Regex;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public record UserUpdatePasswordRequest(
        @Schema(description = "비밀번호", example = "zxc123!@#")
        @Pattern(regexp = Regex.PASSWORD, message = "비밀번호 형식을 확인해주세요.")
        @NotEmpty(message = "비밀번호는 필수입니다.")
        String password,

        @Schema(description = "비밀번호 확인", example = "zxc123!@#")
        @Pattern(regexp = Regex.PASSWORD, message = "비밀번호 형식을 확인해주세요.")
        @NotEmpty(message = "비밀번호 확인은 필수입니다.")
        String passwordCheck
) {

}
