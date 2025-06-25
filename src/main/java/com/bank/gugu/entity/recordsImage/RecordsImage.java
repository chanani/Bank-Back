package com.bank.gugu.entity.recordsImage;

import com.bank.gugu.entity.BaseEntity;
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
@Table(name = "records_image")
public class RecordsImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "records_id")
    private Records records;

    @Column(name = "path")
    private String path;
}
