package com.laboschqpa.server.api.dto.ugc.riddleeditor;

import com.laboschqpa.server.repo.event.dto.RiddleTeamProgressJpaDto;
import lombok.Data;

@Data
public class RiddleTeamProgressResponse {
    private Long teamId;
    private String teamName;
    private Integer solvedRiddleCount;


    public RiddleTeamProgressResponse(RiddleTeamProgressJpaDto jpaDto) {
        this.teamId = jpaDto.getTeamId();
        this.teamName = jpaDto.getTeamName();
        this.solvedRiddleCount = jpaDto.getSolvedRiddleCount();
    }
}
