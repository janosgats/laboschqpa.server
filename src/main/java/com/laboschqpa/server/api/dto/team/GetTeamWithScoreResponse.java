package com.laboschqpa.server.api.dto.team;

import com.laboschqpa.server.repo.dto.TeamWithScoreJpaDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetTeamWithScoreResponse {
    private Long id;
    private String name;
    private Boolean archived;
    private Integer score;

    public GetTeamWithScoreResponse(TeamWithScoreJpaDto teamWithScoreJpaDto) {
        this.id = teamWithScoreJpaDto.getId();
        this.name = teamWithScoreJpaDto.getName();
        this.archived = teamWithScoreJpaDto.getArchived();
        this.score = teamWithScoreJpaDto.getScore();
    }
}
