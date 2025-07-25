package com.bank.gugu.domain.user.repository;

import com.bank.gugu.entity.common.constant.StatusType;
import com.bank.gugu.entity.user.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUserId(String userId);
    boolean existsByEmailAndStatus(String email, StatusType status);
    Optional<User> findByUserIdAndStatus(String userId, StatusType statusType);
    Optional<User> findByIdAndStatus(Long userNo, StatusType statusType);
    Optional<User> findByEmailAndStatus(String email, StatusType statusType);

    boolean existsByUserIdAndEmailAndStatus(String userId, String email, StatusType statusType);
}
