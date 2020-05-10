package com.laboschqpa.server.entity;

import com.laboschqpa.server.entity.account.UserAcc;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "profile_details", indexes = {@Index(columnList = "user_acc_id")})
public class ProfileDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_acc_id", unique = true, nullable = false)
    private UserAcc userAcc;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "nick_name")
    private String nickName;
}
