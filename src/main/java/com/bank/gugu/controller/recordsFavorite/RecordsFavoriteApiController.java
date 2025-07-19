package com.bank.gugu.controller.recordsFavorite;

import com.bank.gugu.domain.recordsFavorite.service.RecordsFavoriteService;
import com.bank.gugu.domain.recordsFavorite.service.dto.request.RecordsFavoriteCreateRequest;
import com.bank.gugu.domain.recordsFavorite.service.dto.request.RecordsFavoriteUpdateRequest;
import com.bank.gugu.entity.user.User;
import com.bank.gugu.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Records Favorite API Controller", description = "입/출금 기록 관련 API를 제공합니다.")
@RestController
@RequiredArgsConstructor
public class RecordsFavoriteApiController {

    private final RecordsFavoriteService recordsFavoriteService;

    @Operation(summary = "입/출금 내역 즐겨찾기 등록 API", description = "입/출금 내역 즐겨찾기를 등록합니다.")
    @PostMapping(value = "/api/v1/user/records-favorite")
    public ResponseEntity<ApiResponse> addRecordsFavorite(
            @Valid @RequestBody RecordsFavoriteCreateRequest request,
            @Parameter(hidden = true)User user
            ) {
        recordsFavoriteService.addRecordsFavorite(request, user);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @Operation(summary = "입/출금 내역 즐겨찾기 삭제 API", description = "입/출금 내역 즐겨찾기를 삭제합니다.")
    @DeleteMapping(value = "/api/v1/user/records-favorite/{recordsFavoriteId}")
    public ResponseEntity<ApiResponse> deleteRecordsFavorite(@PathVariable(name = "recordsFavoriteId") Long recordsFavoriteId) {
        recordsFavoriteService.deleteRecordsFavorite(recordsFavoriteId);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @Operation(summary = "입/출금 내역 즐겨찾기 수정 API", description = "입/출금 내역 즐겨찾기를 수정합니다.")
    @PutMapping(value = "/api/v1/user/records-favorite/{recordsFavoriteId}")
    public ResponseEntity<ApiResponse> updateRecordsFavorite(
            @PathVariable(name = "recordsFavoriteId")  Long recordsFavoriteId,
            @Valid @RequestBody RecordsFavoriteUpdateRequest request,
            @Parameter(hidden = true)User user
    ) {
        recordsFavoriteService.updateRecordsFavorite(recordsFavoriteId, request, user);
        return ResponseEntity.ok(ApiResponse.ok());
    }
}
