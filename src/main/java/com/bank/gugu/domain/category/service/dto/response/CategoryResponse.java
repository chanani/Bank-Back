package com.bank.gugu.domain.category.service.dto.response;

import com.bank.gugu.entity.category.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class CategoryResponse {

    @Schema(description = "카테고리 ID")
    private Long id;

    @Schema(description = "카테고리명")
    private String name;

    @Schema(description = "아이콘 ID")
    private Long iconId;

    @Schema(description = "순서")
    private Integer order;

    public CategoryResponse(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.iconId = category.getIcon().getId();
        this.order = category.getOrders();
    }
}
