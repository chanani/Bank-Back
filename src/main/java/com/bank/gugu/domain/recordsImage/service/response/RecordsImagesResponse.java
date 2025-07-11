package com.bank.gugu.domain.recordsImage.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
public class RecordsImagesResponse {

    @Schema(description = "년-월")
    private String yearMonthDay;

    @Schema(description = "입/출금 이미지 목록")
    private List<RecordsImagesMonthResponse> recordsImages;

    public RecordsImagesResponse(String yearMonthDay, List<RecordsImagesMonthResponse> recordsImages) {
        this.yearMonthDay = yearMonthDay;
        this.recordsImages = recordsImages;
    }
}
