package com.bank.gugu.entity.category;

import com.bank.gugu.entity.BaseEntity;
import com.bank.gugu.entity.common.constant.RecordType;
import com.bank.gugu.entity.icon.Icon;
import com.bank.gugu.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.swing.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@DynamicInsert
@DynamicUpdate
@Builder
@Table(name = "category")
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private RecordType type;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "icon_id")
    private Icon icon;

    @Column(name = "orders")
    private Integer orders;

    public Category(User user, RecordType type, String name, Integer order, Icon icon) {
        this.user = user;
        this.type = type;
        this.name = name;
        this.orders = order;
        this.icon = icon;
    }

    /**
     * 카테고리 수정
     */
    public void update(Category newEntity) {
        if(newEntity.name != null){
            this.name = newEntity.name;
        }
        if(newEntity.icon != null){
            this.icon = newEntity.icon;
        }
        if(newEntity.type != null){
            this.type = newEntity.type;
        }
    }

    /**
     * 순서 변경
     */
    public void updateOrder(Integer requestOrder) {
        this.orders = requestOrder;
    }
}
