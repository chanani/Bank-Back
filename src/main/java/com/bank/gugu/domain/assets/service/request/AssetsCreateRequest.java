package com.bank.gugu.domain.assets.service.request;

import com.bank.gugu.entity.assets.Assets;
import com.bank.gugu.entity.common.constant.BooleanYn;
import com.bank.gugu.entity.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AssetsCreateRequest(
        @Schema(description = "명칭", example = "ISA")
        @NotBlank(message = "명칭은 필수입니다.")
        String name,

        @Schema(description = "색상", example = "#77777")
        @NotBlank(message = "색상은 필수입니다.")
        String color,

        @Schema(description = "잔고", example = "0")
        Integer balance,

        @Schema(description = "자산 총합 여부", example = "true")
        boolean totalActive
) {

    public Assets toEntity(User user){
        return Assets.builder()
                .user(user)
                .name(this.name)
                .color(this.color)
                .balance(this.balance)
                .totalActive(totalActive ? BooleanYn.Y : BooleanYn.N)
                .build();
    }
}
