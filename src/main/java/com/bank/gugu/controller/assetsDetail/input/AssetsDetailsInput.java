package com.bank.gugu.controller.assetsDetail.input;

import com.bank.gugu.domain.assetsDetail.repository.condition.AssetsCondition;
import com.bank.gugu.entity.common.constant.RecordType;
import com.bank.gugu.global.query.record.Range;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AssetsDetailsInput(
        @Schema(description = "계좌 아이디", example = "2")
        Long assetsId,

        @Schema(description = "키워드", example = "입금")
        String keyword,

        @Schema(description = "조회 시작 기간", example = "2025-05-25")
        @NotBlank(message = "조회시작 기간은 필수입니다.")
        String startDate,

        @Schema(description = "조회 마지막 기간", example = "2025-06-25")
        @NotBlank(message = "조회 마지막 기간은 필수입니다.")
        String endDate,

        @Schema(description = "거래유형", example = "ALL")
        @NotNull(message = "거래유형은 필수입니다.")
        RecordType type,

        @Schema(description = "정렬 차순", example = "DESC")
        @NotNull(message = "정렬 차순은 필수입니다.")
        String sort
) {
    public AssetsCondition toCondition() {
        return new AssetsCondition(
                this.assetsId,
                this.keyword,
                this.type,
                this.sort,
                new Range(LocalDate.parse(this.startDate), LocalDate.parse(endDate))
        );
    }

}
