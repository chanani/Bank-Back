package com.bank.gugu.domain.user.service.dto.request;

import com.bank.gugu.entity.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class FindUserIdRequest {
    @Schema(description = "회원 아이디")
    private String userId;

    public FindUserIdRequest(User user) {
        this.userId = user.getUserId();
    }
}
