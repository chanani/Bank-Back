package com.bank.gugu.entity.assets;

import com.bank.gugu.entity.BaseEntity;
import com.bank.gugu.entity.assetsDetail.AssetsDetail;
import com.bank.gugu.entity.common.constant.BooleanYn;
import com.bank.gugu.entity.common.constant.RecordType;
import com.bank.gugu.entity.records.Records;
import com.bank.gugu.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@DynamicInsert
@DynamicUpdate
@Builder
@Table(name = "assets")
public class Assets extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "name")
    private String name;

    @Column(name = "color")
    private String color;

    @Column(name = "balance")
    private Integer balance;

    @Column(name = "orders")
    private Integer orders;

    @Column(name = "total_active")
    @Enumerated(EnumType.STRING)
    private BooleanYn totalActive;

    /**
     * 자산 수정
     */
    public void update(Assets newEntity) {
        if (newEntity.name != null) {
            this.name = newEntity.name;
        }
        if (newEntity.color != null) {
            this.color = newEntity.color;
        }
        if (newEntity.totalActive != null) {
            this.totalActive = newEntity.totalActive;
        }
    }

    /**
     * 잔액 변경
     */
    public void updateBalance(AssetsDetail assetsDetail) {
        if (assetsDetail.getType().equals(RecordType.DEPOSIT)) {
            this.balance = this.balance + assetsDetail.getPrice();
        } else if (assetsDetail.getType().equals(RecordType.WITHDRAW)) {
            this.balance = this.balance - assetsDetail.getPrice();
        }
    }

    /**
     * 입/출금 내역 삭제되 었을 경우 잔액 변경
     */
    public void removeBalance(Records findRecord) {
        if (findRecord.getType().equals(RecordType.DEPOSIT)) {
            this.balance = this.balance - findRecord.getPrice();
        } else if (findRecord.getType().equals(RecordType.WITHDRAW)) {
            this.balance = this.balance + findRecord.getPrice();
        }
    }
}
