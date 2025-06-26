package com.bank.gugu.domain.category.service.dto.request;

import com.bank.gugu.entity.category.Category;
import com.bank.gugu.entity.common.constant.RecordType;
import com.bank.gugu.entity.icon.Icon;
import com.bank.gugu.entity.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryCreateRequest(
        @Schema(description = "카테고리명", example = "기타")
        @NotBlank(message = "카테고리명은 필수입니다.")
        String name,

        @Schema(description = "타입(WITHDRAW, DEPOSIT)", example = "WITHDRAW")
        @NotNull(message = "타입은 필수입니다.")
        RecordType type,

        @Schema(description = "아이콘")
        Integer icon
) {

        public Category toEntity(User user, Icon icon) {
                return Category.builder()
                        .user(user)
                        .type(this.type)
                        .icon(icon)
                        .name(this.name)
                        .build();
        }
}
