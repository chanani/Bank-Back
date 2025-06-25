package com.bank.gugu.domain.assetsDetail.repository;

import com.bank.gugu.entity.assets.Assets;
import com.bank.gugu.entity.assetsDetail.AssetsDetail;
import com.bank.gugu.entity.common.constant.StatusType;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssetsDetailRepository extends JpaRepository<AssetsDetail, Long>, AssetsDetailRepositoryCustom {
    Optional<AssetsDetail> findByIdAndStatus(Long assetsDetailId, StatusType statusType);

    Optional<AssetsDetail> findByRecordIdAndStatus(Long id, StatusType statusType);
}
