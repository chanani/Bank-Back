package com.bank.gugu.domain.recordsImage.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class RecordsImagesMonthResponse {
    @Schema(description = "이미지 ID")
    private Long id;

    @Schema(description = "이미지 경로")
    private String path;

    @Schema(description = "게시글 등록일")
    private LocalDate useDate;

    public RecordsImagesMonthResponse(Long id, String path, LocalDate useDate) {
        this.id = id;
        this.path = path;
        this.useDate = useDate;
    }
}
