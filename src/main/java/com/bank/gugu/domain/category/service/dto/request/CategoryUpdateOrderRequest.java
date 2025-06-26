package com.bank.gugu.domain.category.service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryUpdateOrderRequest(
        @Schema(description = "현재순서", example = "2")
        @NotBlank(message = "현재 순서는 필수입니다.")
        Integer currentOrder,

        @Schema(description = "변경할 순서", example = "5")
        @NotNull(message = "변경할 순서는 필수입니다.")
        Integer requestOrder
) {


}
