package com.bank.gugu.controller.assets;

import com.bank.gugu.domain.assets.service.AssetsService;
import com.bank.gugu.domain.assets.service.request.AssetsCreateRequest;
import com.bank.gugu.domain.assets.service.request.AssetsUpdateRequest;
import com.bank.gugu.domain.assets.service.response.AssetsPageResponse;
import com.bank.gugu.domain.assets.service.response.AssetsResponse;
import com.bank.gugu.entity.user.User;
import com.bank.gugu.global.response.ApiResponse;
import com.bank.gugu.global.response.DataResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Tag(name = "Assets API Controller", description = "자산 관련 API를 제공합니다.")
@RestController
@RequiredArgsConstructor
public class AssetsApiController {

    private final AssetsService assetsService;

    @Operation(summary = "자산그룹 생성 API",
            description = "자산 그룹을 생성합니다.")
    @PostMapping("/api/v1/user/assets")
    public ResponseEntity<ApiResponse> addAssets(
            @Valid @RequestBody AssetsCreateRequest request,
            @Parameter(hidden = true) User user
    ) {
        assetsService.addAssets(request,user);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @Operation(summary = "자산그룹 수정 API",
            description = "자산 그룹을 수정합니다.")
    @PutMapping("/api/v1/user/assets/{assetsId}")
    public ResponseEntity<ApiResponse> updateAssets(
            @PathVariable(name = "assetsId") Long assetsId,
            @Valid @RequestBody AssetsUpdateRequest request
    ) {
        assetsService.updateAssets(request, assetsId);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @Operation(summary = "자산그룹 삭제 API",
            description = "자산 그룹을 삭제합니다.")
    @DeleteMapping("/api/v1/user/assets/{assetsId}")
    public ResponseEntity<ApiResponse> deleteAssets(@PathVariable(name = "assetsId") Long assetsId) {
        assetsService.deleteAssets(assetsId);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @Operation(summary = "자산그룹 목록 조회 API",
            description = "자산 그룹 목록을 조회합니다.")
    @GetMapping("/api/v1/user/assets")
    public ResponseEntity<DataResponse<AssetsPageResponse>> getAssets(@Parameter(hidden = true) User user) {
        AssetsPageResponse assets = assetsService.getAssetsList(user);
        return ResponseEntity.ok(DataResponse.send(assets));
    }

    @Operation(summary = "자산 정보 상세 조회 API",
            description = "자산 정보를 상세 조회합니다.")
    @GetMapping("/api/v1/user/assets/{assetsId}")
    public ResponseEntity<DataResponse<AssetsResponse>> getAssets(@PathVariable(name = "assetsId") Long assetsId) {
        AssetsResponse assets = assetsService.getAssets(assetsId);
        return ResponseEntity.ok(DataResponse.send(assets));
    }
}
