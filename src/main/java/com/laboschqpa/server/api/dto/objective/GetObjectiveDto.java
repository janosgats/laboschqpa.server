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

    private Long creatorUserId;
    private Long editorUserId;
    private Instant creationTime;
    private Instant editTime;


    public GetObjectiveDto(Objective objective) {
        this.id = objective.getId();
        this.description = objective.getDescription();
        this.submittable = objective.getSubmittable();
        this.deadline = objective.getDeadline();

        this.creatorUserId = objective.getCreatorUser().getId();
        this.editorUserId = objective.getEditorUser().getId();
        this.creationTime = objective.getCreationTime();
        this.editTime = objective.getEditTime();
    }
}
