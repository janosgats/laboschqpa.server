package com.laboschqpa.server.entity.qrfight;

import com.laboschqpa.server.entity.Team;
import com.laboschqpa.server.entity.account.UserAcc;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "qr_tag_submission",
        indexes = {
                @Index(columnList = "team_id", name = "team")
        },
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"qr_tag_id", "team_id"}, name = "qr_tag__team__unique")
        }
)
public class QrTagSubmission {
    public QrTagSubmission(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qr_tag_id", nullable = false)
    private QrTag qrTag;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitter_user_id", nullable = false)
    private UserAcc submitterUserAcc;

    @Column(name = "created", columnDefinition = "datetime", nullable = false)
    private Instant created;
}
