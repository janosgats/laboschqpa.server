package com.laboschqpa.server.api.dto.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.enums.TeamRole;
import com.laboschqpa.server.enums.converter.jackson.TeamRoleToValueJacksonConverter;
import com.laboschqpa.server.util.ProfilePicHelper;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UsersPageUserInfoResponse {
    private Long userId;

    private String firstName;
    private String lastName;
    private String nickName;

    private String profilePicUrl;

    private Long teamId;
    @JsonSerialize(converter = TeamRoleToValueJacksonConverter.class)
    private TeamRole teamRole;
    private String teamName;

    public UsersPageUserInfoResponse(UserAcc userAcc) {
        this(userAcc, false);
    }

    public UsersPageUserInfoResponse(UserAcc userAcc, boolean withTeamName) {
        this.userId = userAcc.getId();

        this.firstName = userAcc.getFirstName();
        this.lastName = userAcc.getLastName();
        this.nickName = userAcc.getNickName();

        this.profilePicUrl = ProfilePicHelper.getAvatarUrl(firstName, lastName, nickName);

        if (userAcc.getTeam() != null) {
            this.teamId = userAcc.getTeam().getId();
            if (withTeamName) {
                this.teamName = userAcc.getTeam().getName();
            }
        }
        this.teamRole = userAcc.getTeamRole();
    }
}
