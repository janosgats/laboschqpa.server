package com.laboschqpa.server.entity.account.externalaccountdetail;

import com.laboschqpa.server.entity.account.UserAcc;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(indexes = {@Index(columnList = "user_id")})
public abstract class ExternalAccountDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAcc userAcc;

    public Long getId() {
        return id;
    }

    public UserAcc getUserAcc() {
        return userAcc;
    }

    public void setUserAcc(UserAcc userAcc) {
        this.userAcc = userAcc;
    }
}
