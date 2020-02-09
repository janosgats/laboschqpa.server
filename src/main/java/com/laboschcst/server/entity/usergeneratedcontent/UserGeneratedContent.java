package com.laboschcst.server.entity.usergeneratedcontent;

import com.laboschcst.server.entity.account.UserAcc;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class UserGeneratedContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_owner_user_id", nullable = false)
    private UserAcc originalOwnerUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_owner_user_id", nullable = false)
    private UserAcc currentOwnerUser;

    @Column(name = "creation_time")
    private Instant creationTime;

    @Column(name = "edit_time")
    private Instant editTime;

    public Long getId() {
        return id;
    }

    public UserAcc getOriginalOwnerUser() {
        return originalOwnerUser;
    }

    public void setOriginalOwnerUser(UserAcc originalOwnerUser) {
        this.originalOwnerUser = originalOwnerUser;
    }

    public UserAcc getCurrentOwnerUser() {
        return currentOwnerUser;
    }

    public void setCurrentOwnerUser(UserAcc currentOwnerUser) {
        this.currentOwnerUser = currentOwnerUser;
    }

    public Instant getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Instant creationTime) {
        this.creationTime = creationTime;
    }

    public Instant getEditTime() {
        return editTime;
    }

    public void setEditTime(Instant editTime) {
        this.editTime = editTime;
    }
}
