package com.laboschqpa.server.entity.usergeneratedcontent;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.Instant;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "objective")
@Inheritance(strategy = InheritanceType.JOINED)
public class Objective extends UserGeneratedContent {
    @Column(name = "description")
    private String description;//Possibly Markdown

    /**
     * Indicates if team members can submit a {@link Submission} to this objective.
     */
    @Column(name = "submittable", nullable = false)
    private Boolean submittable;

    @Column(name = "deadline", nullable = false)
    private Instant deadline;
}
