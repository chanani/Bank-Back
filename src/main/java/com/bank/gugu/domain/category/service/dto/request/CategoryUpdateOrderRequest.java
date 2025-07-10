package com.bank.gugu.domain.category.service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CategoryUpdateOrderRequest(
        @Schema(description = "카테고리 ID", example = "1")
        @NotNull(message = "현재 순서는 필수입니다.")
        Long id,

        @Schema(description = "변경할 순서", example = "5")
        @NotNull(message = "변경할 순서는 필수입니다.")
        Integer order
) {


}
