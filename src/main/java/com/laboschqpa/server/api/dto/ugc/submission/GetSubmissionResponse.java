package com.laboschqpa.server.api.dto.ugc.submission;

import com.laboschqpa.server.api.dto.ugc.GetUserGeneratedContentResponse;
import com.laboschqpa.server.entity.usergeneratedcontent.Submission;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetSubmissionResponse extends GetUserGeneratedContentResponse {
    private Long objectiveId;
    private Long teamId;
    private String content;

    public GetSubmissionResponse() {
        super();
    }

    public GetSubmissionResponse(Submission submission) {
        this(submission, false);
    }

    /**
     * @param includeAttachments Set this to {@code false} if the attachments should not be got
     *                           (e.g. to avoid {@link org.hibernate.LazyInitializationException})!
     */
    public GetSubmissionResponse(Submission submission, boolean includeAttachments) {
        super(submission, includeAttachments);
        this.objectiveId = submission.getObjective().getId();
        this.teamId = submission.getTeam().getId();
        this.content = submission.getContent();
    }
}
