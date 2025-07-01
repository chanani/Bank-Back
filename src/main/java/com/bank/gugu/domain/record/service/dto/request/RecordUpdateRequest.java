package com.bank.gugu.domain.record.service.dto.request;

import com.bank.gugu.entity.assets.Assets;
import com.bank.gugu.entity.assetsDetail.AssetsDetail;
import com.bank.gugu.entity.category.Category;
import com.bank.gugu.entity.common.constant.BooleanYn;
import com.bank.gugu.entity.common.constant.PriceType;
import com.bank.gugu.entity.common.constant.RecordType;
import com.bank.gugu.entity.records.Records;
import com.bank.gugu.entity.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record RecordUpdateRequest(
        @Schema(description = "입/출금 타입", example = "DEPOSIT")
        @NotNull(message = "입/출급 타입은 필수입니다.")
        RecordType type,

        @Schema(description = "수단", example = "CHECK_CARD")
        @NotNull(message = "수단은 필수입니다.")
        PriceType priceType,

        @Schema(description = "금액", example = "10000")
        @NotNull(message = "금액은 필수입니다.")
        Integer price,

        @Schema(description = "할부", example = "2")
        Integer Monthly,

        @Schema(description = "메모", example = "마트 장보기")
        String memo,

        @Schema(description = "날짜(yyyy-mm-dd)", example = "2025-06-26")
        String useDate,

        @Schema(description = "카테고리 ID", example = "30")
        Long categoryId,

        @Schema(description = "자산그룹 ID", example = "3")
        Long assetsId,

        @Schema(description = "삭제할 이미지 목록", example = "[1, 2]")
        List<Long> deleteImages
) {

    public Records toEntity(Category category, Assets assets) {
        return Records.builder()
                .category(category)
                .assets(assets)
                .type(this.type)
                .price(this.type.equals(RecordType.DEPOSIT) ? this.price : -this.price)
                .priceType(this.priceType)
                .monthly(this.priceType.equals(PriceType.CARD) ? this.Monthly : null)
                .memo(this.memo)
                .useDate(LocalDate.parse(this.useDate))
                .build();
    }


}
