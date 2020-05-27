package com.laboschqpa.server.api.dto.ugc.objective;

import com.laboschqpa.server.api.dto.ugc.GetUserGeneratedContentDto;
import com.laboschqpa.server.entity.usergeneratedcontent.Objective;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetObjectiveDto extends GetUserGeneratedContentDto {
    private String description;
    private Boolean submittable;
    private Instant deadline;

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
    }
}
