package com.bank.gugu.domain.assets.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
public class AssetsPageResponse {

    @Schema(description = "총자산")
    private Integer totalAssets;

    @Schema(description = "자산 그룹 목록")
    private List<AssetsResponse> assets;

    public AssetsPageResponse(Integer totalAssets, List<AssetsResponse> assets) {
        this.totalAssets = totalAssets;
        this.assets = assets;
    }
}
