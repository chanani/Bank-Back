package com.bank.gugu.domain.assets.service;

import com.bank.gugu.domain.assets.repository.AssetsRepository;
import com.bank.gugu.domain.assets.service.request.AssetsCreateRequest;
import com.bank.gugu.domain.assets.service.request.AssetsUpdateRequest;
import com.bank.gugu.domain.assets.service.response.AssetsPageResponse;
import com.bank.gugu.domain.assets.service.response.AssetsResponse;
import com.bank.gugu.entity.assets.Assets;
import com.bank.gugu.entity.common.constant.BooleanYn;
import com.bank.gugu.entity.common.constant.StatusType;
import com.bank.gugu.entity.user.User;
import com.bank.gugu.global.exception.OperationErrorException;
import com.bank.gugu.global.exception.dto.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DefaultAssetsService implements AssetsService {

    private final AssetsRepository assetsRepository;

    @Override
    @Transactional
    public void addAssets(AssetsCreateRequest request, User user) {
        Assets newEntity = request.toEntity(user);
        assetsRepository.save(newEntity);
    }

    @Override
    @Transactional
    public void updateAssets(AssetsUpdateRequest request, Long assetsId) {
        // 자산 그룹 조회
        Assets findAssets = assetsRepository.findByIdAndStatus(assetsId, StatusType.ACTIVE)
                .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_ASSETS));
        // dto -> entity
        Assets newEntity = request.toEntity();
        // 수정
        findAssets.update(newEntity);
    }

    @Override
    @Transactional
    public void deleteAssets(Long assetsId) {
        // 자산 그룹 조회
        Assets findAssets = assetsRepository.findByIdAndStatus(assetsId, StatusType.ACTIVE)
                .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_ASSETS));
        // 소프트 삭제
        findAssets.remove();
    }

    @Override
    public AssetsPageResponse getAssetsList(User user) {
        // 자산 목록 조회
        List<AssetsResponse> findAssets = assetsRepository.findAllByUserIdAndStatusOrderByOrdersAsc(user.getId(), StatusType.ACTIVE).stream()
                .map(AssetsResponse::new)
                .toList();
        // 총 자산 계산
        int totalAssets = findAssets.stream()
                .filter(dto -> dto.getTotalActive().equals(BooleanYn.Y))
                .mapToInt(AssetsResponse::getBalance).sum();

        return new AssetsPageResponse(totalAssets, findAssets);
    }

    @Override
    public AssetsResponse getAssets(Long assetsId) {
        Assets findAssets = assetsRepository.findByIdAndStatus(assetsId, StatusType.ACTIVE)
                .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_ASSETS));
        return new AssetsResponse(findAssets);
    }
}
