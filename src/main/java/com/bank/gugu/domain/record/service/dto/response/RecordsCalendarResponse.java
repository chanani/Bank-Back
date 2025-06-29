package com.bank.gugu.domain.record.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
public class RecordsCalendarResponse {

    @Schema(description = "누적 수입 금액")
    private Integer totalDeposit;

    @Schema(description = "누적 지출 금액")
    private Integer totalWithdraw;

    @Schema(description = "토탈 금액")
    private Integer totalPrice;

    @Schema(description = "입/출금 내역 목록")
    private List<RecordsCalendarDetail> records;

    public RecordsCalendarResponse(Integer totalDeposit, Integer totalWithdraw, Integer totalPrice, List<RecordsCalendarDetail> records) {
        this.totalDeposit = totalDeposit;
        this.totalWithdraw = totalWithdraw;
        this.totalPrice = totalPrice;
        this.records = records;
    }
}
