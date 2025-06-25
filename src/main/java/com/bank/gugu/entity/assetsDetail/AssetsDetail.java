package com.bank.gugu.entity.assetsDetail;

import com.bank.gugu.entity.BaseEntity;
import com.bank.gugu.entity.assets.Assets;
import com.bank.gugu.entity.common.constant.BooleanYn;
import com.bank.gugu.entity.common.constant.RecordType;
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

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private RecordType type;

    @Column(name = "price")
    private Integer price;

    @Column(name = "balance")
    private Integer balance;

    @Column(name = "use_date")
    private LocalDate useDate;

    @Column(name = "active") // 기록에 표시 여부
    @Enumerated(EnumType.STRING)
    private BooleanYn active;

    @Column(name = "memo")
    private String memo;
}
