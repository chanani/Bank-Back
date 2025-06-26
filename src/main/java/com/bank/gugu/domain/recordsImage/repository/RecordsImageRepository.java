package com.bank.gugu.domain.recordsImage.repository;

import com.bank.gugu.entity.common.constant.StatusType;
import com.bank.gugu.entity.records.Records;
import com.bank.gugu.entity.recordsImage.RecordsImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecordsImageRepository extends JpaRepository<RecordsImage, Long> {
    List<RecordsImage> findAllByRecordsAndStatus(Records findRecord, StatusType statusType);

    Optional<RecordsImage> findByIdAndStatus(Long id, StatusType statusType);
}
