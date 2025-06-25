package com.bank.gugu.domain.assetsDetail.service;

import com.bank.gugu.controller.assetsDetail.input.AssetsDetailsInput;
import com.bank.gugu.domain.assets.repository.AssetsRepository;
import com.bank.gugu.domain.assetsDetail.repository.AssetsDetailRepository;
import com.bank.gugu.domain.assetsDetail.repository.condition.AssetsCondition;
import com.bank.gugu.domain.assetsDetail.service.request.AssetsDetailCreateRequest;
import com.bank.gugu.domain.assetsDetail.service.response.AssetsDetailResponse;
import com.bank.gugu.domain.assetsDetail.service.response.AssetsDetailsResponse;
import com.bank.gugu.entity.assets.Assets;
import com.bank.gugu.entity.assetsDetail.AssetsDetail;
import com.bank.gugu.entity.common.constant.BooleanYn;
import com.bank.gugu.entity.common.constant.StatusType;
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

    @Override
    @Transactional
    public void addAssetsDetail(AssetsDetailCreateRequest request, User user) {
        // 자산 그룹 조회
        Assets findAssets = assetsRepository.findByIdAndStatus(request.assetsId(), StatusType.ACTIVE)
                .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_ASSETS));
        // dto -> entity
        AssetsDetail findAssetsDetail = request.toEntity(user, findAssets);
        // 등록
        AssetsDetail saveAssetsDetail = assetsDetailRepository.save(findAssetsDetail);
        // 자산 그룹 금액 변경
        findAssets.updateBalance(saveAssetsDetail);
        // todo 기록에 표시 여부가 활성화 되어있을 경우 record 테이블에 추가
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
            // todo record에서 조회 후 삭제 진행
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
