package com.bank.gugu.entity.user;

import com.bank.gugu.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Table(name = "user")
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    /**
     * 비밀번호 변경
     */
    public void updatePassword(String password) {
        this.password = new BCryptPasswordEncoder().encode(password);
    }

    /**
     * 회원 정보 변경
     */
    public void updatePInfo(User user) {
        if (user.email != null) {
            this.email = user.getEmail();
        }
    }

}
