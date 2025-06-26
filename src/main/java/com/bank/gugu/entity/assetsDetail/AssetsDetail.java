package com.bank.gugu.entity.assetsDetail;

import com.bank.gugu.entity.BaseEntity;
import com.bank.gugu.entity.assets.Assets;
import com.bank.gugu.entity.category.Category;
import com.bank.gugu.entity.common.constant.BooleanYn;
import com.bank.gugu.entity.common.constant.PriceType;
import com.bank.gugu.entity.common.constant.RecordType;
import com.bank.gugu.entity.records.Records;
import com.bank.gugu.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@DynamicInsert
@DynamicUpdate
@Builder
@Table(name = "assets_detail")
public class AssetsDetail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assets_id")
    private Assets assets;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id")
    private Records record;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private RecordType type;

    @Column(name = "price_type")
    @Enumerated(EnumType.STRING)
    private PriceType priceType;

    @Column(name = "price")
    private Integer price;

    @Column(name = "balance")
    private Integer balance;

    @Column(name = "use_date")
    private LocalDate useDate;

    @Column(name = "active") // 입/출금 내역에 표시 여부
    @Enumerated(EnumType.STRING)
    private BooleanYn active;

    @Column(name = "memo")
    private String memo;

    /**
     * 입/출금 내역 변경되었을 경우 내용 업데이트
     */
    public void updateRecordPrice(Records newEntity) {
        this.balance = this.balance - this.price + newEntity.getPrice();
        // 아래 컬럼 변경
        if (newEntity.getType() != null) {
            this.type = newEntity.getType();
        }
        if (newEntity.getPrice() != null) {
            this.price = newEntity.getPrice();
        }
        if (newEntity.getUseDate() != null) {
            this.useDate = newEntity.getUseDate();
        }
    }

    /**
     * 자산 상세 정보 수정
     */

    public void update(AssetsDetail newEntity) {
         if(newEntity.assets != null){
             this.assets = newEntity.assets;
         }
         if(newEntity.record != null){
             this.record = newEntity.record;
         }
         if (newEntity.type != null) {
             this.type = newEntity.type;
         }
         if (newEntity.price != null) {
             this.price = newEntity.price;
         }
         if (newEntity.balance != null) {
             this.balance = newEntity.balance;
         }
         if (newEntity.useDate != null) {
             this.useDate = newEntity.useDate;
         }
         if (newEntity.memo != null) {
             this.memo = newEntity.memo;
         }
         if(newEntity.active!=null){
             this.active = newEntity.active;
         }
         if(newEntity.category!=null){
             this.category = newEntity.category;
         }
         if(newEntity.priceType!=null){
             this.priceType = newEntity.priceType;
         }
    }

    /**
     * record 추가
     */
    public void updateRecordId(Records records) {
        this.record = records;
    }
}
