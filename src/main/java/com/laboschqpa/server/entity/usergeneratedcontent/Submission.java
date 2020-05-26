package com.laboschqpa.server.entity.usergeneratedcontent;

import com.laboschqpa.server.entity.Team;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "submission",
        indexes = {
                @Index(columnList = "team_id"),
                @Index(columnList = "objective_id")
        })
@DiscriminatorValue(value = "3")
public class Submission extends UserGeneratedContent {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "objective_id", nullable = false)
    private Objective objective;

    @Column(name = "content")
    private String content;//Possibly Markdown
}
