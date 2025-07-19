package com.bank.gugu.domain.recordsFavorite.service.dto.respnose;

import com.bank.gugu.entity.common.constant.PriceType;
import com.bank.gugu.entity.common.constant.RecordType;
import com.bank.gugu.entity.recordsFavorite.RecordsFavorite;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class RecordsFavoritesResponse {

    @Schema(description = "입/출금 내역 ID")
    private Long id;

    @Schema(description = "카테고리 아이콘")
    private String categoryIcon;

    @Schema(description = "타입")
    private RecordType type;

    @Schema(description = "금액")
    private Integer price;

    @Schema(description = "결제 유형")
    private PriceType priceType;

    @Schema(description = "할부")
    private Integer monthly;

    @Schema(description = "메모")
    private String memo;

    @Schema(description = "계좌 ID")
    private Long assetsId;

    @Schema(description = "카테고리 ID")
    private Long categoryId;

    public RecordsFavoritesResponse(RecordsFavorite recordsFavorite) {
        this.id = recordsFavorite.getId();
        this.categoryIcon = recordsFavorite.getCategory().getIcon().getPath();
        this.type = recordsFavorite.getType();
        this.price = recordsFavorite.getPrice();
        this.priceType = recordsFavorite.getPriceType();
        this.monthly = recordsFavorite.getMonthly();
        this.memo = recordsFavorite.getMemo();
        this.assetsId = recordsFavorite.getAssets() == null ? null : recordsFavorite.getAssets().getId();
        this.categoryId = recordsFavorite.getCategory() == null ? null : recordsFavorite.getCategory().getId();
    }
}
