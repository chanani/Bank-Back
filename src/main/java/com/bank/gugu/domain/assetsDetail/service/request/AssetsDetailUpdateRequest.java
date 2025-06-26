package com.bank.gugu.domain.assetsDetail.service.request;

import com.bank.gugu.entity.assets.Assets;
import com.bank.gugu.entity.assetsDetail.AssetsDetail;
import com.bank.gugu.entity.category.Category;
import com.bank.gugu.entity.common.constant.BooleanYn;
import com.bank.gugu.entity.common.constant.PriceType;
import com.bank.gugu.entity.common.constant.RecordType;
import com.bank.gugu.entity.records.Records;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record AssetsDetailUpdateRequest(

        @Schema(description = "입/출금 타입", example = "DEPOSIT")
        RecordType type,

        @Schema(description = "금액", example = "20000")
        Integer price,

        @Schema(description = "날짜(yyyy-mm-dd)", example = "2025-06-25")
        String useDate,

        @Schema(description = "기록에 표시 여부", example = "true")
        boolean active,

        @Schema(description = "메모", example = "늦은 입금")
        String memo,

        @Schema(description = "카테고리", example = "30")
        Long categoryId,

        @Schema(description = "결제 유형", example = "CHECK_CARD")
        PriceType priceType
) {

    public AssetsDetail toEntity(Assets assets, AssetsDetail currentAssetsDetail, Category category) {
        return AssetsDetail.builder()
                .category(category)
                .assets(assets)
                .type(this.type)
                .priceType(this.priceType)
                .price(this.type.equals(RecordType.DEPOSIT) ? this.price : -this.price)
                .balance(currentAssetsDetail.getBalance() - currentAssetsDetail.getPrice() +
                        (this.type.equals(RecordType.DEPOSIT) ?
                                this.price : -this.price))
                .useDate(LocalDate.parse(this.useDate))
                .active(this.active ? BooleanYn.Y : BooleanYn.N)
                .memo(this.memo)
                .build();
    }

    public Records toRecordsEntity(Category category) {
        return Records.builder()
                .type(this.type)
                .price(this.type.equals(RecordType.DEPOSIT) ? this.price : -this.price)
                .priceType(this.priceType)
                .category(category)
                .useDate(LocalDate.parse(this.useDate))
                .memo(this.memo)
                .build();
    }
}
