package com.bank.gugu.controller.assetsDetail;

import com.bank.gugu.controller.assetsDetail.input.AssetsDetailsInput;
import com.bank.gugu.domain.assetsDetail.service.AssetsDetailService;
import com.bank.gugu.domain.assetsDetail.service.request.AssetsDetailCreateRequest;
import com.bank.gugu.domain.assetsDetail.service.request.AssetsDetailUpdateRequest;
import com.bank.gugu.domain.assetsDetail.service.response.AssetsDetailResponse;
import com.bank.gugu.domain.assetsDetail.service.response.AssetsDetailsResponse;
import com.bank.gugu.entity.user.User;
import com.bank.gugu.global.page.PageInput;
import com.bank.gugu.global.response.ApiResponse;
import com.bank.gugu.global.response.DataResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Assets Detail API Controller", description = "자산 상세 정보 관련 API를 제공합니다.")
@RestController
@RequiredArgsConstructor
public class AssetsDetailApiController {

    private final AssetsDetailService assetsDetailService;

    @Operation(summary = "자산 상세내역 생성 API",
            description = "자산 상세내역을 생성합니다.")
    @PostMapping("/api/v1/user/assets-detail")
    public ResponseEntity<ApiResponse> addAssetsDetail(
            @Valid @RequestBody AssetsDetailCreateRequest request,
            @Parameter(hidden = true) User user
    ) {
        assetsDetailService.addAssetsDetail(request, user);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @Operation(summary = "자산 상세내역 수정 API",
            description = "자산 상세내역을 수정합니다.")
    @PutMapping("/api/v1/user/assets-detail/{assetsDetailId}")
    public ResponseEntity<ApiResponse> updateAssetsDetail(
            @PathVariable(name = "assetsDetailId") Long assetsDetailId,
            @Valid @RequestBody AssetsDetailUpdateRequest request,
            @Parameter(hidden = true) User user
    ) {
        assetsDetailService.updateAssetsDetail(assetsDetailId, request, user);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @Operation(summary = "자산 상세내역 삭제 API",
            description = "자산 상세내역을 삭제합니다.")
    @DeleteMapping("/api/v1/user/assets-detail/{assetsDetailId}")
    public ResponseEntity<ApiResponse> deleteAssetsDetail(@PathVariable(name = "assetsDetailId") Long assetsDetailId) {
        assetsDetailService.deleteAssetsDetail(assetsDetailId);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @Operation(summary = "자산 상세내역 목록 조회 API",
            description = """
                    자산 상세내역 목록을 조회합니다.
                    무한스크롤로 제작되었으며, 요청한 사이즈보다 1개 더 반환됩니다.
                    화면에서는 size 만큼 화면에 노출시키고, size 보다 1이클 때 다음 페이지를 요청해주세요.
                    """)
    @GetMapping("/api/v1/user/assets-details")
    public ResponseEntity<Slice<AssetsDetailsResponse>> getAssetsDetails(
            @Parameter(hidden = true) User user,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @ParameterObject @ModelAttribute AssetsDetailsInput input
    ) {
        PageInput pageInput = PageInput.builder().page(page).size(size).build();
        Slice<AssetsDetailsResponse> assetsDetails = assetsDetailService.getAssetsDetails(pageInput, input, user);
        return ResponseEntity.ok(assetsDetails);
    }

    @Operation(summary = "자산 상세내역 조회 API",
            description = "자산 상세내역 조회합니다.")
    @GetMapping("/api/v1/user/assets-details/{assetsDetailId}")
    public ResponseEntity<DataResponse<AssetsDetailResponse>> getAssetsDetail(@PathVariable(name = "assetsDetailId") Long assetsDetailId) {
        AssetsDetailResponse assetsDetails = assetsDetailService.getAssetsDetail(assetsDetailId);
        return ResponseEntity.ok(DataResponse.send(assetsDetails));
    }




}
