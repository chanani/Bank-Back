package com.bank.gugu.domain.recordsFavorite.service.dto.respnose;

import com.bank.gugu.entity.recordsFavorite.RecordsFavorite;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class RecordsFavoritesResponse {

    @Schema(description = "입/출금 내역 ID")
    private Long id;

    @Schema(description = "즐겨찾기명")
    private String title;

    @Schema(description = "카테고리 아이콘")
    private String categoryIcon;

    @Schema(description = "카테고리명")
    private String categoryName;



    public RecordsFavoritesResponse(RecordsFavorite recordsFavorite) {
        this.id = recordsFavorite.getId();
        this.categoryIcon = recordsFavorite.getCategory().getIcon().getPath();
        this.categoryName = recordsFavorite.getCategory().getName();
        this.title = recordsFavorite.getTitle();
    }
}
