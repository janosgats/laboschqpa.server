package com.laboschqpa.server.api.dto.team;

import com.laboschqpa.server.entity.Team;
import com.laboschqpa.server.util.SelfValidator;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetTeamDto {
    private Long id;
    private String name;
    private Boolean archived;

    public GetTeamDto(Team team) {
        this.id = team.getId();
        this.name = team.getName();
        this.archived = team.getArchived();
    }
}
