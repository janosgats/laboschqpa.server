package com.laboschqpa.server.entity.usergeneratedcontent;

import com.laboschqpa.server.entity.account.UserAcc;
import lombok.Data;

import javax.persistence.*;
import java.time.Instant;

@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class UserGeneratedContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_user_id", nullable = false)
    private UserAcc creatorUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "editor_user_id", nullable = false)
    private UserAcc editorUser;

    @Column(name = "creation_time")
    private Instant creationTime;

    @Column(name = "edit_time")
    private Instant editTime;
}
