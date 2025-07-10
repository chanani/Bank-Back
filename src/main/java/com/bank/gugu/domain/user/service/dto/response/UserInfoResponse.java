package com.bank.gugu.domain.user.service.dto.response;

import com.bank.gugu.entity.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class UserInfoResponse {
    @Schema(description = "아이디")
    private String userId;

    @Schema(description = "이메일")
    private String email;

    public UserInfoResponse(User user) {
        this.userId = user.getUserId();
        this.email = user.getEmail();
    }
}
