package com.labosch.csillagtura.entity;

import com.labosch.csillagtura.entity.externalaccount.ExternalAccountDetail;
import com.labosch.csillagtura.entity.externalaccount.GithubExternalAccountDetail;
import com.labosch.csillagtura.entity.externalaccount.GoogleExternalAccountDetail;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "userAcc")
public class UserAcc implements Serializable {
    static final long serialVersionUID = 42L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column
    private Boolean enabled;

    @ManyToOne(fetch = FetchType.EAGER)
    private UserAcc joinedInto;//The account which this account is joined in. Null if this account was not joined in another one.

    @OneToMany(mappedBy = "userAcc", cascade = {CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<UserEmailAddress> userEmailAddresses = new HashSet<>();


    @OneToMany(mappedBy = "userAcc", cascade = {CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<ExternalAccountDetail> externalAccountDetails = new HashSet<>();

    @OneToMany(mappedBy = "userAcc", cascade = {CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<GoogleExternalAccountDetail> googleExternalAccountDetails = new HashSet<>();

    @OneToMany(mappedBy = "userAcc", cascade = {CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<GithubExternalAccountDetail> githubExternalAccountDetails = new HashSet<>();


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "approverUserAcc", cascade = {CascadeType.REMOVE})
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<AccountJoinInitiation> accountJoinInitiationsToApprove = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "initiatorUserAcc", cascade = {CascadeType.REMOVE})
    @OnDelete(action = OnDeleteAction.CASCADE)
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

    public Set<UserEmailAddress> getUserEmailAddresses() {
        return userEmailAddresses;
    }

    public UserAcc getJoinedInto() {
        return joinedInto;
    }

    public void setJoinedInto(UserAcc joinedInto) {
        this.joinedInto = joinedInto;
    }

    public Set<AccountJoinInitiation> getAccountJoinInitiationsToApprove() {
        return accountJoinInitiationsToApprove;
    }

    public AccountJoinInitiation getInitiatedAccountJoinInitiation() {
        return initiatedAccountJoinInitiation;
    }

    public void setInitiatedAccountJoinInitiation(AccountJoinInitiation initiatedAccountJoinInitiationsToApprove) {
        this.initiatedAccountJoinInitiation = initiatedAccountJoinInitiationsToApprove;
    }

    public boolean equalsById(UserAcc otherUserAcc) {
        return this.getId() != null
                && this.getId().equals(otherUserAcc.getId());
    }

    public Set<GoogleExternalAccountDetail> getGoogleExternalAccountDetails() {
        return googleExternalAccountDetails;
    }

    public Set<GithubExternalAccountDetail> getGithubExternalAccountDetails() {
        return githubExternalAccountDetails;
    }

    public Set<ExternalAccountDetail> getExternalAccountDetails() {
        return externalAccountDetails;
    }
}