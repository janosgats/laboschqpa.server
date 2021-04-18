package com.laboschqpa.server.api.dto.ugc.submission;

import com.laboschqpa.server.api.dto.ugc.GetUserGeneratedContentResponse;
import com.laboschqpa.server.entity.usergeneratedcontent.Submission;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetSubmissionResponse extends GetUserGeneratedContentResponse {
    private Long objectiveId;
    private Long teamId;
    private String content;

    private String objectiveTitle;
    private Instant objectiveDeadline;
    private Boolean objectiveSubmittable;
    private String teamName;

    public GetSubmissionResponse() {
        super();
    }

    public GetSubmissionResponse(Submission submission) {
        this(submission, false, false);
    }

    /**
     * @param includeAttachments Set this to {@code false} if the attachments should not be got
     *                           (e.g. to avoid {@link org.hibernate.LazyInitializationException})!
     */
    public GetSubmissionResponse(Submission submission, boolean includeDisplayEntities, boolean includeAttachments) {
        super(submission, includeAttachments);
        this.objectiveId = submission.getObjective().getId();
        this.teamId = submission.getTeam().getId();
        this.content = submission.getContent();

        if (includeDisplayEntities) {
            this.objectiveTitle = submission.getObjective().getTitle();
            this.objectiveDeadline = submission.getObjective().getDeadline();
            this.objectiveSubmittable = submission.getObjective().getSubmittable();
            this.teamName = submission.getTeam().getName();
        }
    }
}
