package com.bank.gugu.controller.record;

import com.bank.gugu.domain.record.service.RecordsService;
import com.bank.gugu.domain.record.service.dto.request.RecordCreateRequest;
import com.bank.gugu.domain.record.service.dto.request.RecordUpdateMemoRequest;
import com.bank.gugu.domain.record.service.dto.request.RecordUpdateRequest;
import com.bank.gugu.domain.record.service.dto.response.RecordResponse;
import com.bank.gugu.domain.record.service.dto.response.RecordsCalendarResponse;
import com.bank.gugu.domain.record.service.dto.response.RecordsCurrentResponse;
import com.bank.gugu.domain.record.service.dto.response.RecordsResponse;
import com.bank.gugu.entity.user.User;
import com.bank.gugu.global.response.ApiResponse;
import com.bank.gugu.global.response.DataResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "Records API Controller", description = "입/출금 관련 API를 제공합니다.")
@RestController
@RequiredArgsConstructor
public class RecordsApiController {

    private final RecordsService recordsService;

    @Operation(summary = "입/출금 내역 등록 API", description = "입/출금 내역을 등록합니다.")
    @PostMapping(value = "/api/v1/user/records", consumes = {
            MediaType.MULTIPART_FORM_DATA_VALUE,
            MediaType.APPLICATION_JSON_VALUE
    })
    public ResponseEntity<ApiResponse> addRecord(
            @Valid @RequestPart RecordCreateRequest request,
            @Parameter(hidden = true) User user,
            @RequestPart(value = "files", required = false) List<MultipartFile> inputFiles
    ) throws IOException {
        recordsService.addRecord(request, user, inputFiles);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @Operation(summary = "입/출금 내역 삭제 API", description = "입/출금 내역을 삭제합니다.")
    @DeleteMapping("/api/v1/user/records/{recordsId}")
    public ResponseEntity<ApiResponse> deleteRecord(@PathVariable(name = "recordsId") Long recordsId) {
        recordsService.deleteRecord(recordsId);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @Operation(summary = "입/출금 내역 수정 API", description = "입/출금 내역을 수정합니다.")
    @PutMapping(value = "/api/v1/user/records/{recordsId}", consumes = {
            MediaType.MULTIPART_FORM_DATA_VALUE,
            MediaType.APPLICATION_JSON_VALUE
    })
    public ResponseEntity<ApiResponse> updateRecord(
            @Valid @RequestPart RecordUpdateRequest request,
            @PathVariable(name = "recordsId") Long recordsId,
            @RequestPart(value = "files", required = false) List<MultipartFile> inputFiles,
            @Parameter(hidden = true) User user
    ) throws IOException {
        recordsService.updateRecord(request, recordsId, inputFiles, user);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @Operation(summary = "입/출금 하루 내역 조회 API", description = "하루의 입/출금 내역을 조회합니다.")
    @GetMapping("/api/v1/user/records-current")
    public ResponseEntity<DataResponse<List<RecordsCurrentResponse>>> getRecordsCurrent(
            @Parameter(name = "currentDate") String currentDate, @Parameter(hidden = true) User user
    ) {
        List<RecordsCurrentResponse> records = recordsService.getCurrentRecord(LocalDate.parse(currentDate), user);
        return ResponseEntity.ok(DataResponse.send(records));
    }

    @Operation(summary = "입/출금 상세 내역 조회 API", description = "입/출금 상세 내역을 조회합니다.")
    @GetMapping("/api/v1/user/records/{recordsId}")
    public ResponseEntity<DataResponse<RecordResponse>> getRecord(@PathVariable(name = "recordsId") Long recordsId) {
        RecordResponse record = recordsService.getRecord(recordsId);
        return ResponseEntity.ok(DataResponse.send(record));
    }

    @Operation(summary = "입/출금 한달 내역 조회 API", description = "한달 입/출금 내역을 조회합니다.")
    @GetMapping("/api/v1/user/records")
    public ResponseEntity<DataResponse<List<RecordsResponse>>> getRecords(
            @Parameter(name = "yearMonth") String yearMonth, @Parameter(hidden = true) User user
    ) {
        List<RecordsResponse> records = recordsService.getMonthRecord(yearMonth, user);
        return ResponseEntity.ok(DataResponse.send(records));
    }

    @Operation(summary = "입/출금 메모 수정 API", description = "입/출금 메모를 수정합니다.")
    @PutMapping("/api/v1/user/records-memo/{recordsId}")
    public ResponseEntity<ApiResponse> updateMemo(
            @PathVariable(name = "recordsId") Long recordsId,
            @Valid @RequestBody RecordUpdateMemoRequest request
    ) {
        recordsService.updateMemo(recordsId, request);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @Operation(summary = "캘린더의 입/출금 내역 및 총 금액 조회 API", description = "캘린더의 입/출금 내역 및 총 금액을 조회합니다.")
    @GetMapping("/api/v1/user/records-calendar")
    public ResponseEntity<DataResponse<RecordsCalendarResponse>> getCalendarRecords(
            @Parameter(name = "yearMonth", example = "2025-06") String yearMonth, @Parameter(hidden = true) User user
    ) {
        RecordsCalendarResponse records = recordsService.getCalendarRecord(yearMonth, user);
        return ResponseEntity.ok(DataResponse.send(records));
    }

}
