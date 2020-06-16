package com.laboschqpa.server.api.dto.ugc.objective;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.laboschqpa.server.api.dto.ugc.GetUserGeneratedContentDto;
import com.laboschqpa.server.entity.usergeneratedcontent.Objective;
import com.laboschqpa.server.enums.converter.jackson.ObjectiveTypeToValueJacksonConverter;
import com.laboschqpa.server.enums.ugc.ObjectiveType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetObjectiveDto extends GetUserGeneratedContentDto {
    private String description;
    private Boolean submittable;
    private Instant deadline;
    @JsonSerialize(converter = ObjectiveTypeToValueJacksonConverter.class)
    private ObjectiveType objectiveType;
    private Boolean scored;

    public GetObjectiveDto() {
        super();
    }

    public GetObjectiveDto(Objective objective) {
        this(objective, false);
    }

    /**
     * @param includeAttachments Set this to {@code false} if the attachments should not be got
     *                           (e.g. to avoid {@link org.hibernate.LazyInitializationException})!
     */
    public GetObjectiveDto(Objective objective, boolean includeAttachments) {
        super(objective, includeAttachments);
        this.description = objective.getDescription();
        this.submittable = objective.getSubmittable();
        this.deadline = objective.getDeadline();
        this.objectiveType = objective.getObjectiveType();
        this.scored = objective.getScored();
    }
}
