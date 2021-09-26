package com.laboschqpa.server.repo.dto;

import com.laboschqpa.server.enums.TeamRole;
import com.laboschqpa.server.model.ProfilePicUrlContainer;

public interface TeamMemberJpaDto extends ProfilePicUrlContainer {
    Long getUserId();

    String getFirstName();

    String getLastName();

    String getNickName();

    @Override
    String getProfilePicUrl();

    TeamRole getTeamRole();
}
