package com.laboschqpa.server.entity.usergeneratedcontent;

import com.laboschqpa.server.entity.account.UserAcc;
import lombok.Data;

import javax.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.INTEGER)
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

    @Column(name = "creation_time", columnDefinition = "datetime")
    private Instant creationTime;

    @Column(name = "edit_time", columnDefinition = "datetime")
    private Instant editTime;

    @ElementCollection(fetch = FetchType.LAZY)
    @JoinTable(name = "attachment",
            joinColumns = {
                    @JoinColumn(name = "ugc_id", nullable = false)
            },
            foreignKey = @ForeignKey(name = "fk_ugc_attachment",
                    foreignKeyDefinition = "foreign key (ugc_id) references user_generated_content (id) on delete cascade"
            ),
            indexes = @Index(columnList = "indexed_file_id", name = "indexed_file_id")
    )
    @Column(name = "indexed_file_id", nullable = false)
    private Set<Long> attachments = new HashSet<>();

    public void setUGCAsCreatedByUser(UserAcc creatorUserAcc) {
        this.setCreatorUser(creatorUserAcc);
        this.setEditorUser(creatorUserAcc);
        this.setCreationTime(Instant.now());
        this.setEditTime(Instant.now());
    }

    public void setUGCAsEditedByUser(UserAcc editorUserAcc) {
        this.setEditorUser(editorUserAcc);
        this.setEditTime(Instant.now());
    }
}

