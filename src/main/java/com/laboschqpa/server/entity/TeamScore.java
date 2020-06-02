package com.laboschqpa.server.entity;

import com.laboschqpa.server.entity.usergeneratedcontent.Objective;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "team_score",
        indexes = {
                @Index(columnList = "objective_id", name = "objective_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"team_id", "objective_id"}, name = "team_id__objective_id__unique")
        }
)
public class TeamScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "objective_id", nullable = false)
    private Objective objective;

    @Column(name = "score", nullable = false)
    private Integer score;
}
