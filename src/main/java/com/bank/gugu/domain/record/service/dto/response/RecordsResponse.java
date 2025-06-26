package com.bank.gugu.domain.record.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
public class RecordsResponse {

    @Schema(description = "년-월")
    private String yearMonth;

    @Schema(description = "입/출금 내역 목록")
    private List<RecordsMonthResponse> records;

    public RecordsResponse(String yearMonth, List<RecordsMonthResponse> records) {
        this.yearMonth = yearMonth;
        this.records = records;
    }
}
