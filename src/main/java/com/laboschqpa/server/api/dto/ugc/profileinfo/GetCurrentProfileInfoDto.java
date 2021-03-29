package com.laboschqpa.server.api.dto.ugc.profileinfo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.enums.auth.Authority;
import com.laboschqpa.server.enums.auth.TeamRole;
import com.laboschqpa.server.enums.converter.jackson.TeamRoleToValueJacksonConverter;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class GetCurrentProfileInfoDto {
    private Long id;
    private Boolean enabled;

    @JsonSerialize(converter = TeamRoleToValueJacksonConverter.class)
    private TeamRole teamRole;
    private Long teamId;

    private String firstName;
    private String lastName;
    private String nickName;

    private Set<Authority> authorities = new HashSet<>();

    public GetCurrentProfileInfoDto(UserAcc userAcc) {
        this.id = userAcc.getId();
        this.enabled = userAcc.getEnabled();

        this.teamRole = userAcc.getTeamRole();
        if (userAcc.getTeam() != null) {
            this.teamId = userAcc.getTeam().getId();
        }

        this.firstName = userAcc.getFirstName();
        this.lastName = userAcc.getLastName();
        this.nickName = userAcc.getNickName();

        this.authorities = userAcc.getAuthorities();
    }
}
