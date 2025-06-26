package com.bank.gugu.domain.graph.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class GraphCategories {

    @Schema(description = "카테고리 ID")
    private Long id;

    @Schema(description = "카테고리명")
    private String name;

    @Schema(description = "아이콘 경로")
    private String iconPath;

    @Schema(description = "건수")
    private Integer useCount;

    @Schema(description = "금액")
    private Integer sumPrice;

    @Schema(description = "퍼센트")
    private Double percent;

    public GraphCategories(Long id, String name, String iconPath, Integer sumPrice, Integer useCount, Double percent) {
        this.id = id;
        this.name = name;
        this.iconPath = iconPath;
        this.sumPrice = sumPrice;
        this.useCount = useCount;
        this.percent = percent;
    }

    @Override
    public String toString() {
        return "GraphCategories{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", iconPath='" + iconPath + '\'' +
                ", useCount=" + useCount +
                ", percent=" + percent +
                '}';
    }
}
