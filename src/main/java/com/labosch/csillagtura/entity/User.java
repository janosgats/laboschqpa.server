package com.labosch.csillagtura.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="user")
public class User implements Serializable {
    static final long serialVersionUID = 42L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column
    private Boolean enabled;

    @ManyToOne(fetch = FetchType.EAGER)
    private User joinedInto;//The account which this account is joined in. Null if this account was not joined in another one.

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserEmailAddress> userEmailAddresses = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "approverUser")
    private List<AccountJoinInitiation> accountJoinInitiationsToApprove = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "initiatorUser")
    private AccountJoinInitiation initiatedAccountJoinInitiation;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<UserEmailAddress> getUserEmailAddresses() {
        return userEmailAddresses;
    }

    public User getJoinedInto() {
        return joinedInto;
    }

    public void setJoinedInto(User joinedInto) {
        this.joinedInto = joinedInto;
    }

    public List<AccountJoinInitiation> getAccountJoinInitiationsToApprove() {
        return accountJoinInitiationsToApprove;
    }

    public AccountJoinInitiation getInitiatedAccountJoinInitiation() {
        return initiatedAccountJoinInitiation;
    }

    public void setInitiatedAccountJoinInitiation(AccountJoinInitiation initiatedAccountJoinInitiationsToApprove) {
        this.initiatedAccountJoinInitiation = initiatedAccountJoinInitiationsToApprove;
    }
}