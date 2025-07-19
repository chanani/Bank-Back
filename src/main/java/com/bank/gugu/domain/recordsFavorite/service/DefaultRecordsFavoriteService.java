package com.bank.gugu.domain.recordsFavorite.service;

import com.bank.gugu.domain.assets.repository.AssetsRepository;
import com.bank.gugu.domain.category.repository.CategoryRepository;
import com.bank.gugu.domain.recordsFavorite.repository.RecordsFavoriteRepository;
import com.bank.gugu.domain.recordsFavorite.service.dto.request.RecordsFavoriteCreateRequest;
import com.bank.gugu.entity.assets.Assets;
import com.bank.gugu.entity.category.Category;
import com.bank.gugu.entity.common.constant.StatusType;
import com.bank.gugu.entity.recordsFavorite.RecordsFavorite;
import com.bank.gugu.entity.user.User;
import com.bank.gugu.global.exception.OperationErrorException;
import com.bank.gugu.global.exception.dto.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DefaultRecordsFavoriteService implements RecordsFavoriteService {

    private final RecordsFavoriteRepository recordsFavoriteRepository;
    private final CategoryRepository categoryRepository;
    private final AssetsRepository assetsRepository;

    @Override
    @Transactional
    public void addRecordsFavorite(RecordsFavoriteCreateRequest request, User user) {
        // 카테고리 Entity 조회
        Category findCategory = categoryRepository.findByIdAndStatus(request.categoryId(), StatusType.ACTIVE)
                .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_CATEGORY));
        // 자산 Entity 조회
        Assets findAssets = null;
        if(request.assetsId() != null){
            findAssets = assetsRepository.findByIdAndStatus(request.assetsId(), StatusType.ACTIVE)
                    .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_ASSETS));
        }
        // dto to Entity
        RecordsFavorite newEntity = request.toEntity(user, findCategory, findAssets);
        recordsFavoriteRepository.save(newEntity);
    }

    @Override
    @Transactional
    public void deleteRecordsFavorite(Long recordsFavoriteId) {
        // 즐겨찾기 조회
        RecordsFavorite findRecordsFavorite = recordsFavoriteRepository.findByIdAndStatus(recordsFavoriteId, StatusType.ACTIVE)
                .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_RECORDS_FAVORITE));
        // 소프트 삭제
        findRecordsFavorite.remove();
    }
}
