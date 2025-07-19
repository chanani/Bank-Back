package com.bank.gugu.domain.recordsFavorite.repository;

import com.bank.gugu.entity.common.constant.StatusType;
import com.bank.gugu.entity.recordsFavorite.RecordsFavorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecordsFavoriteRepository extends JpaRepository<RecordsFavorite, Long> {
    Optional<RecordsFavorite> findByIdAndStatus(Long recordsFavoriteId, StatusType statusType);
}
