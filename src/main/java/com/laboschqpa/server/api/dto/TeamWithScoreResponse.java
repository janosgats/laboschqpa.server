package com.laboschqpa.server.api.dto;

import com.laboschqpa.server.repo.dto.TeamWithScoreJpaDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TeamWithScoreResponse {
    private Long id;
    private String name;
    private Boolean archived;
    private Integer score;

    public TeamWithScoreResponse(TeamWithScoreJpaDto jpaDto) {
        this.id = jpaDto.getId();
        this.name = jpaDto.getName();
        this.archived = jpaDto.getArchived();
        this.score = jpaDto.getScore();
    }
}
