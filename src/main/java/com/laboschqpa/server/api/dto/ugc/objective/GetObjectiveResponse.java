package com.laboschqpa.server.api.dto.ugc.objective;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.laboschqpa.server.api.dto.ugc.GetUserGeneratedContentResponse;
import com.laboschqpa.server.entity.usergeneratedcontent.Objective;
import com.laboschqpa.server.enums.converter.jackson.ObjectiveTypeToValueJacksonConverter;
import com.laboschqpa.server.enums.ugc.ObjectiveType;
import com.laboschqpa.server.repo.usergeneratedcontent.dto.GetObjectiveJpaDto;
import com.laboschqpa.server.repo.usergeneratedcontent.dto.GetObjectiveWithTeamScoreJpaDto;
import com.laboschqpa.server.repo.usergeneratedcontent.dto.ObjectiveDtoAdapter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetObjectiveResponse extends GetUserGeneratedContentResponse {
    private Long programId;
    private String title;
    private String description;
    private Boolean submittable;
    private Instant deadline;
    private Instant hideSubmissionsBefore;
    @JsonSerialize(converter = ObjectiveTypeToValueJacksonConverter.class)
    private ObjectiveType objectiveType;
    private Boolean isFraction;
    private Boolean observerTeamHasScore;

    public GetObjectiveResponse() {
        super();
    }

    public GetObjectiveResponse(Objective objective) {
        this(objective, false);
    }

    /**
     * @param includeAttachments Set this to {@code false} if the attachments should not be got
     *                           (e.g. to avoid {@link org.hibernate.LazyInitializationException})!
     */
    public GetObjectiveResponse(Objective objective, boolean includeAttachments) {
        this(new ObjectiveDtoAdapter(objective), includeAttachments);
    }

    /**
     * @param includeAttachments Set this to {@code false} if the attachments should not be got
     *                           (e.g. to avoid {@link org.hibernate.LazyInitializationException})!
     */
    public GetObjectiveResponse(GetObjectiveJpaDto objective, boolean includeAttachments) {
        super(objective, includeAttachments);
        this.programId = objective.getProgramId();
        this.title = objective.getTitle();
        this.description = objective.getDescription();
        this.submittable = objective.getSubmittable();
        this.deadline = objective.getDeadline();
        this.hideSubmissionsBefore = objective.getHideSubmissionsBefore();
        this.objectiveType = objective.getObjectiveType();
        this.isFraction = objective.getIsFraction();

        if (objective instanceof GetObjectiveWithTeamScoreJpaDto) {
            final Integer observerTeamScore = ((GetObjectiveWithTeamScoreJpaDto) objective).getObserverTeamScore();
            this.observerTeamHasScore = observerTeamScore != null && observerTeamScore > 0;
        }
    }
}
