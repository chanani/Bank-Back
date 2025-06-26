package com.bank.gugu.domain.category.repository;

import com.bank.gugu.entity.category.Category;
import com.bank.gugu.entity.common.constant.RecordType;
import com.bank.gugu.entity.common.constant.StatusType;
import com.bank.gugu.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByIdAndStatus(Long categoryId, StatusType statusType);

    List<Category> findByUserIdAndTypeAndStatus(Long id, RecordType type, StatusType statusType);

    Optional<Category> findByUserAndOrder(User user, Integer currentOrder);

    List<Category> findByUserAndOrderBetween(User user, int currentOrder, Integer requestOrder);
}
