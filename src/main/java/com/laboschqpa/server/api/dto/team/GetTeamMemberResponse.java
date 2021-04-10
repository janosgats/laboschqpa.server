package com.laboschqpa.server.api.dto.team;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.laboschqpa.server.enums.TeamRole;
import com.laboschqpa.server.enums.converter.jackson.TeamRoleToValueJacksonConverter;
import com.laboschqpa.server.repo.dto.TeamMemberJpaDto;
import com.laboschqpa.server.util.ProfilePicHelper;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetTeamMemberResponse {
    private Long userId;
    private String firstName;
    private String lastName;
    private String nickName;
    private String profilePicUrl;
    @JsonSerialize(converter = TeamRoleToValueJacksonConverter.class)
    private TeamRole teamRole;

    public GetTeamMemberResponse(TeamMemberJpaDto teamMemberJpaDto) {
        this.userId = teamMemberJpaDto.getUserId();
        this.firstName = teamMemberJpaDto.getFirstName();
        this.lastName = teamMemberJpaDto.getLastName();
        this.nickName = teamMemberJpaDto.getNickName();
        this.teamRole = teamMemberJpaDto.getTeamRole();

        this.profilePicUrl = ProfilePicHelper.getAvatarUrl(firstName, lastName, nickName);
    }
}
