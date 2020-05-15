package com.laboschqpa.server.api.dto.submission;

import com.laboschqpa.server.entity.usergeneratedcontent.Submission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GetSubmissionDto {
    private Long id;
    private Long objectiveId;
    private Long teamId;
    private String content;

    public GetSubmissionDto(Submission submission) {
        this.id = submission.getId();
        this.objectiveId = submission.getObjective().getId();
        this.teamId = submission.getTeam().getId();
        this.content = submission.getContent();
    }
}
