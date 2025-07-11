package com.bank.gugu.controller.recordsImage;

import com.bank.gugu.domain.recordsImage.service.RecordsImageService;
import com.bank.gugu.domain.recordsImage.service.response.RecordsImagesResponse;
import com.bank.gugu.entity.user.User;
import com.bank.gugu.global.response.ApiResponse;
import com.bank.gugu.global.response.DataResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Tag(name = "Records Image API Controller", description = "입/출금 이미지 관련 API를 제공합니다.")
@RestController
@RequiredArgsConstructor
public class RecordsImageApiController {

    private final RecordsImageService recordsImageService;

    @Operation(summary = "입/출금 내역 이미지 파일 삭제 API", description = "입/출금 내역에 등록된 이미지 파일을 삭제합니다.")
    @DeleteMapping(value = "/api/v1/user/records-image/{recordsImageId}")
    public ResponseEntity<ApiResponse> deleteRecord(@PathVariable(name = "recordsImageId") Long recordsImageId) {
        recordsImageService.deleteRecordImage(recordsImageId);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @Operation(summary = "입/출금 내역 이미지 파일 등록 API", description = "입/출금 내역에 등록된 이미지 파일을 등록합니다.")
    @PostMapping(value = "/api/v1/user/records-image/{recordsId}", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse> addRecord(
            @PathVariable(name = "recordsId") Long recordsId,
            @RequestPart(value = "uploadImage") MultipartFile inputFile,
            @Parameter(hidden = true) User user
    ) {
        recordsImageService.addRecordImage(recordsId, inputFile, user);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @Operation(summary = "입/출금 내역 이미지 파일 조회 API", description = "입/출금 내역에 등록된 월별 이미지 목록을 조회합니다.")
    @GetMapping(value = "/api/v1/user/records-image")
    public ResponseEntity<DataResponse<List<RecordsImagesResponse>>> getRecordsImage(
            @Parameter(name = "yearMonth") String yearMonth,
            @Parameter(hidden = true) User user
    ) {
        List<RecordsImagesResponse> recordsImages = recordsImageService.getRecordsImages(yearMonth, user);
        return ResponseEntity.ok(DataResponse.send(recordsImages));
    }


}
