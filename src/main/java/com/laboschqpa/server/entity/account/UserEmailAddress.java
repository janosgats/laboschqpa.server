package com.laboschqpa.server.entity.account;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user_email_address",
        indexes = {
                @Index(columnList = "email"),
                @Index(columnList = "user_id")
        })
public class UserEmailAddress implements Serializable {
    static final long serialVersionUID = 42L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAcc userAcc;

    @Column(name = "email", unique = true)
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserAcc getUserAcc() {
        return userAcc;
    }

    public void setUserAcc(UserAcc userAcc) {
        this.userAcc = userAcc;
    }

    public Long getId() {
        return id;
    }
}
