package com.bank.gugu.entity.recordsFavorite;

import com.bank.gugu.entity.BaseEntity;
import com.bank.gugu.entity.assets.Assets;
import com.bank.gugu.entity.category.Category;
import com.bank.gugu.entity.common.constant.PriceType;
import com.bank.gugu.entity.common.constant.RecordType;
import com.bank.gugu.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;

@Entity
@DynamicInsert
@DynamicUpdate
@Getter
@Table(name = "records_favorite")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RecordsFavorite extends BaseEntity {

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

    @Column(name = "title")
    private String title;

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

    /**
     * 즐겨찾기 정보 수정
     */
    public void update(RecordsFavorite newEntity) {
        if(newEntity.title != null) {
            this.title = newEntity.title;
        }
        if (newEntity.memo != null) {
            this.memo = newEntity.memo;
        }
        if (newEntity.price != null) {
            this.price = newEntity.price;
        }
        if (newEntity.priceType != null) {
            this.priceType = newEntity.priceType;
        }
        if (newEntity.category != null) {
            this.category = newEntity.category;
        }
        if (newEntity.monthly != null) {
            this.monthly = newEntity.monthly;
        }
        if (newEntity.type != null) {
            this.type = newEntity.type;
        }
        this.assets = newEntity.assets;
    }
}
