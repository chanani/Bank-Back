package com.bank.gugu.controller.category;

import com.bank.gugu.domain.category.service.CategoryService;
import com.bank.gugu.domain.category.service.dto.request.CategoryCreateRequest;
import com.bank.gugu.domain.category.service.dto.request.CategoryUpdateRequest;
import com.bank.gugu.domain.category.service.dto.response.CategoriesResponse;
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

@Tag(name = "Category API Controller", description = "카테고리 관련 API를 제공합니다.")
@RestController
@RequiredArgsConstructor
public class CategoryApiController {

    private final CategoryService categoryService;

    @Operation(summary = "카테고리 등록 API",
            description = "카테고리를 등록합니다.")
    @PostMapping("/api/v1/user/categories")
    public ResponseEntity<ApiResponse> addCategory(
            @Valid @RequestBody CategoryCreateRequest request,
            @Parameter(hidden = true) User user
    ) {
        categoryService.addCategory(request, user);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @Operation(summary = "카테고리 수정 API",
            description = "카테고리를 수정합니다.")
    @PutMapping("/api/v1/user/categories/{categoryId}")
    public ResponseEntity<ApiResponse> updateCategory(
            @PathVariable(name = "categoryId") Long categoryId,
            @Valid @RequestBody CategoryUpdateRequest request,
            @Parameter(hidden = true) User user
    ) {
        categoryService.updateCategory(categoryId, request, user);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @Operation(summary = "카테고리 삭제 API",
            description = "카테고리를 삭제합니다.")
    @DeleteMapping("/api/v1/user/categories/{categoryId}")
    public ResponseEntity<ApiResponse> updateCategory(@PathVariable(name = "categoryId") Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @Operation(summary = "카테고리 조회 API",
            description = "카테고리를 조회합니다.")
    @GetMapping("/api/v1/user/categories")
    public ResponseEntity<DataResponse<List<CategoriesResponse>>> updateCategory(
            @Parameter(hidden = true) User user,
            @Parameter(name = "type") RecordType type
    ) {
        List<CategoriesResponse> categories = categoryService.getCategories(user, type);
        return ResponseEntity.ok(DataResponse.send(categories));
    }

    // todo 카테고리 위치 변경


}
