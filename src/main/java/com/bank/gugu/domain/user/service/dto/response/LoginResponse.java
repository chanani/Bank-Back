package com.bank.gugu.domain.user.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {

    @Schema(description = "accessToken")
    private String accessToken;

    @Schema(description = "refreshToken")
    private String refreshToken;

}
