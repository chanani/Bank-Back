package com.bank.gugu.entity;

import com.bank.gugu.entity.common.constant.StatusType;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseCreatedEntity {

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(name = "created_by")
    private Long createdBy;

    @ColumnDefault("ACTICE")
    private StatusType status;

    /**
     * state = DELETED 처리 (삭제처리)
     * */
    public void remove() {
        this.status = StatusType.DELETED;
    }
}
