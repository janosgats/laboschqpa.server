package com.laboschqpa.server.repo.dto;

import com.laboschqpa.server.enums.TeamRole;

public interface TeamMemberJpaDto {
    Long getUserId();

    String getFirstName();

    String getLastName();

    String getNickName();

    TeamRole getTeamRole();
}
