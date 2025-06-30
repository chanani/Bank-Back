package com.bank.gugu.domain.record.service.dto.response;

import com.bank.gugu.entity.common.constant.PriceType;
import com.bank.gugu.entity.common.constant.RecordType;
import com.bank.gugu.entity.records.Records;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class RecordResponse {

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

    @Schema(description = "날짜")
    private LocalDate useDate;

    @Schema(description = "이미지 목록")
    private List<RecordImagesResponse> recordImages;

    @Schema(description = "계좌 ID")
    private Long assetsId;

    @Schema(description = "카테고리 ID")
    private Long categoryId;

    public RecordResponse(Records record, List<RecordImagesResponse> images) {
        this.id = record.getId();
        this.categoryIcon = record.getCategory().getIcon().getPath();
        this.type = record.getType();
        this.price = record.getPrice();
        this.priceType = record.getPriceType();
        this.monthly = record.getMonthly();
        this.memo = record.getMemo();
        this.useDate = record.getUseDate();
        this.recordImages = images;
        this.assetsId = record.getAssets().getId();
        this.categoryId = record.getCategory().getId();
    }
}
