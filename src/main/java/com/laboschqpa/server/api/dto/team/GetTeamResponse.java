package com.laboschqpa.server.api.dto.team;

import com.laboschqpa.server.entity.Team;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetTeamResponse {
    private Long id;
    private String name;
    private Boolean archived;

    public GetTeamResponse(Team team) {
        this.id = team.getId();
        this.name = team.getName();
        this.archived = team.getArchived();
    }
}
