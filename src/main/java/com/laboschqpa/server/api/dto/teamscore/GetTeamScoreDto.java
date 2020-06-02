package com.laboschqpa.server.api.dto.teamscore;

import com.laboschqpa.server.entity.TeamScore;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class GetTeamScoreDto {
    private Long id;
    private Long objectiveId;
    private Long teamId;
    private Integer score;

    public GetTeamScoreDto(TeamScore teamScore) {
        this.id = teamScore.getId();
        this.objectiveId = teamScore.getObjective().getId();
        this.teamId = teamScore.getTeam().getId();
        this.score = teamScore.getScore();
    }
}
