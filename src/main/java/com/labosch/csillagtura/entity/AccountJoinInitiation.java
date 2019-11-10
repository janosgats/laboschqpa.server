package com.labosch.csillagtura.entity;

import javax.persistence.*;

@Entity
@Table(name = "account_join_initiation")
public class AccountJoinInitiation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JoinColumn(name = "initiator_user_id", unique = true, nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private UserAcc initiatorUserAcc;

    @JoinColumn(name = "approver_user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private UserAcc approverUserAcc;

    @Column(name = "approved", nullable = false)
    private boolean approved = false;

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public UserAcc getApproverUserAcc() {
        return approverUserAcc;
    }

    public void setApproverUserAcc(UserAcc approverUserAcc) {
        this.approverUserAcc = approverUserAcc;
    }

    public UserAcc getInitiatorUserAcc() {
        return initiatorUserAcc;
    }

    public void setInitiatorUserAcc(UserAcc initiatorUserAcc) {
        this.initiatorUserAcc = initiatorUserAcc;
    }

    public Long getId() {
        return id;
    }
}
