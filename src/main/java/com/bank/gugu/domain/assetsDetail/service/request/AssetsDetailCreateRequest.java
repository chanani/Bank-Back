package com.bank.gugu.domain.assetsDetail.service.request;

import com.bank.gugu.entity.assets.Assets;
import com.bank.gugu.entity.assetsDetail.AssetsDetail;
import com.bank.gugu.entity.category.Category;
import com.bank.gugu.entity.common.constant.BooleanYn;
import com.bank.gugu.entity.common.constant.PriceType;
import com.bank.gugu.entity.common.constant.RecordType;
import com.bank.gugu.entity.records.Records;
import com.bank.gugu.entity.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AssetsDetailCreateRequest(
        @Schema(description = "자산 그룹 ID", example = "5")
        @NotNull(message = "자산 그룹 번호는 필수입니다.")
        Long assetsId,

        @Schema(description = "입/출금 타입", example = "DEPOSIT")
        @NotNull(message = "입/출금 타입은 필수입니다.")
        RecordType type,

        @Schema(description = "금액", example = "20000")
        @NotNull(message = "금액은 필수입니다.")
        Integer price,

        @Schema(description = "날짜(yyyy-mm-dd)", example = "2025-06-25")
        @NotBlank(message = "날짜는 필수입니다.")
        String useDate,

        @Schema(description = "카테고리", example = "30")
        @NotNull(message = "카테고리는 필수입니다.")
        Long categoryId,

        @Schema(description = "결제 유형", example = "CHECK_CARD")
        @NotNull(message = "결제 유형은 필수입니다.")
        PriceType priceType,

        @Schema(description = "기록에 표시 여부", example = "true")
        @NotNull(message = "기록에 표시 여부는 필수입니다.")
        boolean active,

        @Schema(description = "메모", example = "늦은 입금")
        String memo
) {

    public AssetsDetail toEntity(User user, Assets assets, Category category) {
        return AssetsDetail.builder()
                .user(user)
                .assets(assets)
                .category(category)
                .type(this.type)
                .priceType(this.priceType)
                .price(this.price)
                .balance(this.type.equals(RecordType.DEPOSIT) ?
                        assets.getBalance() + price :
                        assets.getBalance() - price)
                .useDate(LocalDate.parse(this.useDate))
                .active(this.active ? BooleanYn.Y : BooleanYn.N)
                .memo(this.memo)
                .build();
    }

    public Records toRecordEntity(User user, Assets  assets, Category category) {
        return Records.builder()
                .user(user)
                .category(category)
                .assets(assets)
                .type(this.type)
                .price(price)
                .priceType(this.priceType)
                .memo(this.memo)
                .useDate(LocalDate.parse(this.useDate))
                .build();
    }
}
