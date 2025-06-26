package com.bank.gugu.domain.graph.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
public class GraphsResponse {
    @Schema(description = "총 금액")
    private Integer balance;

    @Schema(description = "카테고리별 목록")
    private List<GraphCategories>  categories;

    public GraphsResponse(Integer balance, List<GraphCategories> categories) {
        this.balance = balance;
        this.categories = categories;
    }
}
