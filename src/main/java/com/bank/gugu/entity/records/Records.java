package com.bank.gugu.entity.records;

import com.bank.gugu.entity.BaseEntity;
import com.bank.gugu.entity.assets.Assets;
import com.bank.gugu.entity.category.Category;
import com.bank.gugu.entity.common.constant.PriceType;
import com.bank.gugu.entity.common.constant.RecordType;
import com.bank.gugu.entity.common.constant.StatusType;
import com.bank.gugu.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.util.Date;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@DynamicInsert
@DynamicUpdate
@Builder
@Table(name = "records")
public class Records extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assets_id")
    private Assets assets;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private RecordType type;

    @Column(name = "price")
    private Integer price;

    @Column(name = "price_type")
    @Enumerated(EnumType.STRING)
    private PriceType priceType;

    @Column(name = "monthly")
    private Integer monthly;

    @Column(name = "memo")
    private String memo;

    @Column(name = "use_date")
    private LocalDate useDate;

    /**
     * 입/출금 내역 수정
     */
    public void update(Records newEntity) {
        if (newEntity.category != null) {
            this.category = newEntity.category;
        }
        if (newEntity.assets != null) {
            this.assets = newEntity.assets;
        }
        if (newEntity.type != null) {
            this.type = newEntity.type;
        }
        if (newEntity.price != null) {
            this.price = newEntity.price;
        }
        if (newEntity.monthly != null) {
            this.monthly = newEntity.priceType.equals(PriceType.CARD) ? newEntity.monthly : null;
        }
        if (newEntity.memo != null) {
            this.memo = newEntity.memo;
        }
        if (newEntity.useDate != null) {
            this.useDate = newEntity.useDate;
        }
        if(newEntity.priceType != null) {
            this.priceType = newEntity.priceType;
        }
    }
}
