package com.bank.gugu.domain.record.service;

import com.bank.gugu.domain.assets.repository.AssetsRepository;
import com.bank.gugu.domain.assetsDetail.repository.AssetsDetailRepository;
import com.bank.gugu.domain.category.repository.CategoryRepository;
import com.bank.gugu.domain.record.repository.RecordsRepository;
import com.bank.gugu.domain.record.service.dto.request.RecordCreateRequest;
import com.bank.gugu.domain.record.service.dto.request.RecordUpdateRequest;
import com.bank.gugu.domain.user.repository.UserRepository;
import com.bank.gugu.entity.BaseEntity;
import com.bank.gugu.entity.assets.Assets;
import com.bank.gugu.entity.assetsDetail.AssetsDetail;
import com.bank.gugu.entity.category.Category;
import com.bank.gugu.entity.common.constant.StatusType;
import com.bank.gugu.entity.records.Records;
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
public class DefaultRecordsService implements RecordsService {

    private final RecordsRepository recordsRepository;
    private final UserRepository userRepository;
    private final AssetsRepository assetsRepository;
    private final CategoryRepository categoryRepository;
    private final AssetsDetailRepository assetsDetailRepository;

    @Override
    @Transactional
    public void addRecord(RecordCreateRequest request, User user) {
        // 카테고리 조회
        Category findCategory = categoryRepository.findByIdAndStatus(request.categoryId(), StatusType.ACTIVE)
                .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_CATEGORY));
        // assets entity 조회
        Assets findAssets = null;
        if (request.assetsId() != null && request.assetsId() > 0) {
            findAssets = assetsRepository.findByIdAndStatus(request.assetsId(), StatusType.ACTIVE)
                    .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_ASSETS));
        }
        // dto -> entity
        Records newEntity = request.toEntity(user, findCategory, findAssets);
        // 등록
        Records saveRecord = recordsRepository.save(newEntity);

        // 자산 그룹을 지정했을 경우 자산 상세 내용 등록
        if (request.assetsId() != null && request.assetsId() > 0) {
            // dto -> assets Entity
            AssetsDetail newAssetsDetailEntity = request.toAssetsDetail(user, findAssets, saveRecord);
            // 등록
            assetsDetailRepository.save(newAssetsDetailEntity);
            // 자산 그룹 합계 금액 업데이트
            findAssets.updateBalance(newAssetsDetailEntity);
        }
    }

    @Override
    @Transactional
    public void deleteRecord(Long recordsId) {
        // record Entity 조회
        Records findRecord = recordsRepository.findByIdAndStatus(recordsId, StatusType.ACTIVE)
                .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_RECORDS));
        // 소프트 삭제
        findRecord.remove();

        // 자산에 등록 되어있을 경우 내역 삭제 및 금액 차감
        if (findRecord.getAssets() != null) {
            assetsRepository.findByIdAndStatus(findRecord.getAssets().getId(), StatusType.ACTIVE)
                    .ifPresent(findAssets -> findAssets.removeBalance(findRecord));
            assetsDetailRepository.findByRecordIdAndStatus(findRecord.getId(), StatusType.ACTIVE)
                    .ifPresent(BaseEntity::remove);
        }
    }

    @Override
    @Transactional
    public void updateRecord(RecordUpdateRequest request, Long recordsId) {
        // 카테고리 조회
        Category findCategory = categoryRepository.findByIdAndStatus(request.categoryId(), StatusType.ACTIVE)
                .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_CATEGORY));
        // assets entity 조회
        Assets findAssets = null;
        if (request.assetsId() != null && request.assetsId() > 0) {
            findAssets = assetsRepository.findByIdAndStatus(request.assetsId(), StatusType.ACTIVE)
                    .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_ASSETS));
        }
        // dto -> entity
        Records newEntity = request.toEntity(findCategory, findAssets);
        // record Entity 조회
        Records findRecord = recordsRepository.findByIdAndStatus(recordsId, StatusType.ACTIVE)
                .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_RECORDS));
        // 자산 내역 수정
        if (findRecord.getAssets() != null) {
            assetsRepository.findByIdAndStatus(findRecord.getAssets().getId(), StatusType.ACTIVE)
                    .ifPresent(assets -> assets.updateRecordBalance(findRecord, newEntity));
            assetsDetailRepository.findByRecordIdAndStatus(findRecord.getId(), StatusType.ACTIVE)
                    .ifPresent(assetsDetail -> assetsDetail.updateRecordPrice(newEntity));
        }
        // 수정
        findRecord.update(newEntity);
    }
}
