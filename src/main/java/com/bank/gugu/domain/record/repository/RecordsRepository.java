package com.bank.gugu.domain.record.repository;

import com.bank.gugu.entity.common.constant.StatusType;
import com.bank.gugu.entity.records.Records;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecordsRepository extends JpaRepository<Records, Long> {
    Optional<Records> findByIdAndStatus(Long recordsId, StatusType statusType);
}
