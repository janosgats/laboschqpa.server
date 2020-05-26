package com.laboschqpa.server.api.dto.ugc.submission;

import com.laboschqpa.server.api.dto.ugc.GetUserGeneratedContentDto;
import com.laboschqpa.server.entity.usergeneratedcontent.Submission;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetSubmissionDto extends GetUserGeneratedContentDto {
    private Long id;
    private Long objectiveId;
    private Long teamId;
    private String content;

    public GetSubmissionDto() {
        super();
    }

    public GetSubmissionDto(Submission submission) {
        this(submission, false);
    }

    /**
     * @param includeAttachments Set this to {@code false} if the attachments should not be get
     *                           (e.g. to avoid {@link org.hibernate.LazyInitializationException})!
     */
    public GetSubmissionDto(Submission submission, boolean includeAttachments) {
        super(submission, includeAttachments);
        this.id = submission.getId();
        this.objectiveId = submission.getObjective().getId();
        this.teamId = submission.getTeam().getId();
        this.content = submission.getContent();
    }
}
