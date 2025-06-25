package com.bank.gugu.controller.record;

import com.bank.gugu.domain.category.service.dto.request.CategoryCreateRequest;
import com.bank.gugu.domain.category.service.dto.request.CategoryUpdateRequest;
import com.bank.gugu.domain.category.service.dto.response.CategoriesResponse;
import com.bank.gugu.domain.record.service.RecordsService;
import com.bank.gugu.domain.record.service.dto.request.RecordCreateRequest;
import com.bank.gugu.domain.record.service.dto.request.RecordUpdateRequest;
import com.bank.gugu.entity.common.constant.RecordType;
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

import java.util.List;

@Tag(name = "Records API Controller", description = "기록 관련 API를 제공합니다.")
@RestController
@RequiredArgsConstructor
public class RecordsApiController {

    private final RecordsService recordsService;

    @Operation(summary = "입/출금 내역 등록 API",
            description = "입/출금 내역을 등록합니다.")
    @PostMapping("/api/v1/user/records")
    public ResponseEntity<ApiResponse> addRecord(
            @Valid @RequestBody RecordCreateRequest request,
            @Parameter(hidden = true) User user
    ) {
        recordsService.addRecord(request, user);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @Operation(summary = "입/출금 내역 삭제 API",
            description = "입/출금 내역을 삭제합니다.")
    @DeleteMapping("/api/v1/user/records/{recordsId}")
    public ResponseEntity<ApiResponse> deleteRecord(
            @PathVariable(name = "recordsId") Long recordsId
    ) {
        recordsService.deleteRecord(recordsId);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @Operation(summary = "입/출금 내역 수정 API",
            description = "입/출금 내역을 수정합니다.")
    @PutMapping("/api/v1/user/records/{recordsId}")
    public ResponseEntity<ApiResponse> updateRecord(
            @Valid @RequestBody RecordUpdateRequest request,
            @PathVariable(name = "recordsId") Long recordsId
    ) {
        recordsService.updateRecord(request, recordsId);
        return ResponseEntity.ok(ApiResponse.ok());
    }



}
