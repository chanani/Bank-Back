package com.bank.gugu.controller.recordsFavorite;

import com.bank.gugu.domain.recordsFavorite.service.RecordsFavoriteService;
import com.bank.gugu.domain.recordsFavorite.service.dto.request.RecordsFavoriteCreateRequest;
import com.bank.gugu.entity.user.User;
import com.bank.gugu.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}
