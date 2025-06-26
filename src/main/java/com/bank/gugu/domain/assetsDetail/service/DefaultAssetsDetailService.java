package com.bank.gugu.domain.assetsDetail.service;

import com.bank.gugu.controller.assetsDetail.input.AssetsDetailsInput;
import com.bank.gugu.domain.assets.repository.AssetsRepository;
import com.bank.gugu.domain.assetsDetail.repository.AssetsDetailRepository;
import com.bank.gugu.domain.assetsDetail.repository.condition.AssetsCondition;
import com.bank.gugu.domain.assetsDetail.service.request.AssetsDetailCreateRequest;
import com.bank.gugu.domain.assetsDetail.service.request.AssetsDetailUpdateRequest;
import com.bank.gugu.domain.assetsDetail.service.response.AssetsDetailResponse;
import com.bank.gugu.domain.assetsDetail.service.response.AssetsDetailsResponse;
import com.bank.gugu.domain.category.repository.CategoryRepository;
import com.bank.gugu.domain.record.repository.RecordsRepository;
import com.bank.gugu.entity.assets.Assets;
import com.bank.gugu.entity.assetsDetail.AssetsDetail;
import com.bank.gugu.entity.category.Category;
import com.bank.gugu.entity.common.constant.BooleanYn;
import com.bank.gugu.entity.common.constant.StatusType;
import com.bank.gugu.entity.records.Records;
import com.bank.gugu.entity.user.User;
import com.bank.gugu.global.exception.OperationErrorException;
import com.bank.gugu.global.exception.dto.ErrorCode;
import com.bank.gugu.global.page.PageInput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DefaultAssetsDetailService implements AssetsDetailService {

    private final AssetsRepository assetsRepository;
    private final AssetsDetailRepository assetsDetailRepository;
    private final RecordsRepository recordsRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public void addAssetsDetail(AssetsDetailCreateRequest request, User user) {
        // 자산 그룹 조회
        Assets findAssets = assetsRepository.findByIdAndStatus(request.assetsId(), StatusType.ACTIVE)
                .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_ASSETS));
        // 카테고리 조회
        Category findCategory = categoryRepository.findByIdAndStatus(request.categoryId(), StatusType.ACTIVE)
                .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_CATEGORY));
        // dto -> entity
        AssetsDetail findAssetsDetail = request.toEntity(user, findAssets, findCategory);
        // 등록
        AssetsDetail saveAssetsDetail = assetsDetailRepository.save(findAssetsDetail);
        // 자산 그룹 금액 변경
        findAssets.updateBalance(saveAssetsDetail);
        // 입/출금 내역 생성
        Records newRecordEntity = request.toRecordEntity(user, findAssets, findCategory);
        if (!request.active()) newRecordEntity.inactive(); // 활성화 안할 경우 비노출

        Records saveRecord = recordsRepository.save(newRecordEntity);

        // assetsDetail에 RecordId 추가
        saveAssetsDetail.updateRecordId(saveRecord);
    }

    @Override
    @Transactional
    public void updateAssetsDetail(Long assetsDetailId, AssetsDetailUpdateRequest request, User user) {
        // 상세 정보 조회
        AssetsDetail findAssetsDetail = assetsDetailRepository.findByIdAndStatus(assetsDetailId, StatusType.ACTIVE)
                .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_ASSETS_DETAIL));
        // 자산 그룹 조회
        Assets findAssets = assetsRepository.findByIdAndStatus(findAssetsDetail.getAssets().getId(), StatusType.ACTIVE)
                .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_ASSETS));
        // 카테고리 조회
        Category findCategory = categoryRepository.findByIdAndStatus(request.categoryId(), StatusType.ACTIVE)
                .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_CATEGORY));
        // dto -> entity
        AssetsDetail newEntity = request.toEntity(findAssets, findAssetsDetail, findCategory);
        // 입/출금 내역 조회
        Records findRecord = recordsRepository.findById(findAssetsDetail.getRecord().getId())
                .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_RECORDS));
        Records newRecordEntity = request.toRecordsEntity(findCategory);
        // 자산 그룹 금액 변경
        findAssets.updateRecordBalance(findRecord, newRecordEntity);
        // 입/출금 내역 수정
        findRecord.update(newRecordEntity);
        // 입/출금 내역에서 숨김 및 보이기 활성화
        findRecord.updateActive(request.active());
        // 수정 진행
        findAssetsDetail.update(newEntity);
    }

    @Override
    @Transactional
    public void deleteAssetsDetail(Long assetsDetailId) {
        // 상세 정보 조회
        AssetsDetail findAssetsDetail = assetsDetailRepository.findByIdAndStatus(assetsDetailId, StatusType.ACTIVE)
                .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_ASSETS_DETAIL));
        // 소프트 삭제
        findAssetsDetail.remove();
        // 기록에 표시되어 있는 상태일 경우 기록도 삭제
        if (findAssetsDetail.getActive().equals(BooleanYn.Y)) {
            // 입/출금 내역 조회 후 수정
            Records findRecord = recordsRepository.findById(findAssetsDetail.getRecord().getId())
                    .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_RECORDS));
            findRecord.remove();
        }
    }

    @Override
    public Slice<AssetsDetailsResponse> getAssetsDetails(PageInput pageInput, AssetsDetailsInput input, User user) {
        // input -> condition
        AssetsCondition condition = input.toCondition();
        // 페이징 객체 생성
        Pageable pageable = Pageable.ofSize(pageInput.size() + 1).withPage(pageInput.page() - 1);
        // 자산 상세내역 조회
        List<AssetsDetailsResponse> assetsDetails = assetsDetailRepository.findByQuery(pageable, condition, user).stream()
                .map(AssetsDetailsResponse::new)
                .toList();
        // 반환할 페이지 객체 생성
        Pageable returnPageable = pageable.withPage(pageInput.page());
        // Slice 객체 생성
        Slice<AssetsDetailsResponse> pointDetailSlice = new SliceImpl<>(assetsDetails, returnPageable, hasNextPage(assetsDetails, pageable.getPageSize()));
        return pointDetailSlice;
    }

    @Override
    public AssetsDetailResponse getAssetsDetail(Long assetsDetailId) {
        // 상세 정보 조회
        AssetsDetail findAssetsDetail = assetsDetailRepository.findByIdAndStatus(assetsDetailId, StatusType.ACTIVE)
                .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_ASSETS_DETAIL));
        return new AssetsDetailResponse(findAssetsDetail);
    }

    /**
     * Slice에서 사용할 메서드
     */
    private boolean hasNextPage(List<AssetsDetailsResponse> pointDetails, int pageSize) {
        if (pointDetails.size() > pageSize) {
            pointDetails.remove(pageSize);
            return true;
        }
        return false;
    }
}
