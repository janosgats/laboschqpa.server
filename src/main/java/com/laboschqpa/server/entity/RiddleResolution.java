package com.laboschqpa.server.entity;

import com.laboschqpa.server.entity.usergeneratedcontent.Riddle;
import com.laboschqpa.server.enums.attributeconverter.RiddleResolutionStatusAttributeConverter;
import com.laboschqpa.server.enums.riddle.RiddleResolutionStatus;
import lombok.Data;

import javax.persistence.*;
import java.time.Instant;

@Data
@Entity
@Table(name = "riddle_resolution",
        indexes = {
                @Index(columnList = "riddle_id, solving_timestamp", name = "riddle_id__solving_timestamp"),
                @Index(columnList = "team_id, status, riddle_id", name = "team_id__status__riddle_id"),
        },
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"team_id", "riddle_id"}, name = "team_id__riddle_id__unique")
        }
)
public class RiddleResolution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "riddle_id", nullable = false)
    private Riddle riddle;

    @Convert(converter = RiddleResolutionStatusAttributeConverter.class)
    @Column(name = "status", nullable = false)
    private RiddleResolutionStatus status = RiddleResolutionStatus.UNSOLVED;

    @Column(name = "hint_used", nullable = false)
    private Boolean hintUsed;

    @Column(name = "solving_timestamp")
    private Instant solvingTimestamp;
}
