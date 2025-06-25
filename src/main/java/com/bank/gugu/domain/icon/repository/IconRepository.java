package com.bank.gugu.domain.icon.repository;

import com.bank.gugu.entity.common.constant.StatusType;
import com.bank.gugu.entity.icon.Icon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IconRepository extends JpaRepository<Icon, Integer> {
    Optional<Icon> findByIdAndStatus(Integer icon, StatusType statusType);
}
