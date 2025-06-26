package com.bank.gugu.domain.record.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
public class RecordsCalendarDetail {

    @Schema(description = "일 수")
    private Integer day;

    @Schema(description = "지출 금액")
    private Integer dayDeposit;

    @Schema(description = "수입 금액")
    private Integer dayWithdraw;

    @Override
    public String toString() {
        return "RecordsCalendarDetail{" +
                "day=" + day +
                ", dayDeposit=" + dayDeposit +
                ", dayWithdraw=" + dayWithdraw +
                '}';
    }

    public RecordsCalendarDetail(Integer day, Integer dayDeposit, Integer dayWithdraw) {
        this.day = day;
        this.dayDeposit = dayDeposit;
        this.dayWithdraw = dayWithdraw;
    }
}
