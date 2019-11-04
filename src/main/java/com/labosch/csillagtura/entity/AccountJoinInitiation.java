package com.labosch.csillagtura.entity;

import javax.persistence.*;

@Entity
public class AccountJoinInitiation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JoinColumn(name = "initiator_user_id", unique = true, nullable = false)
    @OneToOne(fetch = FetchType.EAGER)
    private User initiatorUser;

    @JoinColumn(name = "approver_user_id", nullable = false)
    @OneToOne(fetch = FetchType.EAGER)
    private User approverUser;

    @Column(name="approved", nullable = false)
    private boolean approved = false;

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public User getApproverUser() {
        return approverUser;
    }

    public void setApproverUser(User approverUser) {
        this.approverUser = approverUser;
    }

    public User getInitiatorUser() {
        return initiatorUser;
    }

    public void setInitiatorUser(User initiatorUser) {
        this.initiatorUser = initiatorUser;
    }

    public Long getId() {
        return id;
    }
}
