package com.bank.gugu.controller.icon;

import com.bank.gugu.domain.icon.service.IconService;
import com.bank.gugu.domain.icon.service.dto.response.IconsResponse;
import com.bank.gugu.domain.recordsImage.service.RecordsImageService;
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


@Tag(name = "Icon API Controller", description = "아이콘 관련 API를 제공합니다.")
@RestController
@RequiredArgsConstructor
public class IconApiController {

    private final IconService iconService;

    @Operation(summary = "아이콘 목록 조회 API", description = "아이콘 목록을 조회합니다.")
    @GetMapping(value = "/api/v1/user/icons")
    public ResponseEntity<DataResponse<List<IconsResponse>>> getIcons() {
        List<IconsResponse> icons = iconService.getIcons();
        return ResponseEntity.ok(DataResponse.send(icons));
    }



}
