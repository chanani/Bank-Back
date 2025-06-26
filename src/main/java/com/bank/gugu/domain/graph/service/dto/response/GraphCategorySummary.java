package com.bank.gugu.domain.graph.service.dto.response;

import lombok.Getter;

@Getter
public class GraphCategorySummary {
    private Long categoryId;
    private String categoryName;
    private String iconPath;
    private Long useCountRaw;
    private Integer sumPriceRaw;

    public GraphCategorySummary(Long categoryId, String categoryName, String iconPath, Long useCountRaw, Integer sumPriceRaw) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.iconPath = iconPath;
        this.useCountRaw = useCountRaw;
        this.sumPriceRaw = sumPriceRaw;
    }
}
