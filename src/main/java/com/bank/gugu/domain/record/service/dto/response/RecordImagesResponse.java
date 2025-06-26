package com.bank.gugu.domain.record.service.dto.response;

import com.bank.gugu.entity.recordsImage.RecordsImage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;


@Getter
public class RecordImagesResponse {

    @Schema(description = "이미지 ID")
    private Long id;

    @Schema(description = "이미지 경로")
    private String path;

    public RecordImagesResponse(RecordsImage recordsImage) {
        this.id = recordsImage.getId();
        this.path = recordsImage.getPath();
    }
}
