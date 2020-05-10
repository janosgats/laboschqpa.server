package com.laboschqpa.server.api.dto.objective;

import com.laboschqpa.server.entity.usergeneratedcontent.Objective;
import lombok.Data;

import java.time.Instant;

@Data
public class GetObjectiveDto {
    private Long id;
    private String description;
    private Boolean submittable;
    private Instant deadline;

    public GetObjectiveDto(Objective objective) {
        this.id = objective.getId();
        this.description = objective.getDescription();
        this.submittable = objective.getSubmittable();
        this.deadline = objective.getDeadline();
    }
}
