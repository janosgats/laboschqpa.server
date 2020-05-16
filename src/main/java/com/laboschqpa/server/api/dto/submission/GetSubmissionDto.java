package com.laboschqpa.server.api.dto.submission;

import com.laboschqpa.server.entity.usergeneratedcontent.Submission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GetSubmissionDto {
    private Long id;
    private Long objectiveId;
    private Long teamId;
    private String content;

    private Long creatorUserId;
    private Long editorUserId;
    private Instant creationTime;
    private Instant editTime;

    public GetSubmissionDto(Submission submission) {
        this.id = submission.getId();
        this.objectiveId = submission.getObjective().getId();
        this.teamId = submission.getTeam().getId();
        this.content = submission.getContent();

        this.creatorUserId = submission.getCreatorUser().getId();
        this.editorUserId = submission.getEditorUser().getId();
        this.creationTime = submission.getCreationTime();
        this.editTime = submission.getEditTime();
    }
}
