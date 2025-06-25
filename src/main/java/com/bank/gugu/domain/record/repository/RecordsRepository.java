package com.bank.gugu.domain.record.repository;

import com.bank.gugu.entity.records.Records;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordsRepository extends JpaRepository<Records, Long> {
}
