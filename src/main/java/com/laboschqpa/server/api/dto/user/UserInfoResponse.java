package com.laboschqpa.server.api.dto.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.enums.TeamRole;
import com.laboschqpa.server.enums.auth.Authority;
import com.laboschqpa.server.enums.converter.jackson.TeamRoleToValueJacksonConverter;
import com.laboschqpa.server.util.ProfilePicHelper;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.Set;

@AllArgsConstructor
@Data
public class UserInfoResponse {
    private Long userId;

    private String firstName;
    private String lastName;
    private String nickName;

    private Instant registered;

    private String profilePicUrl;

    private Long teamId;
    @JsonSerialize(converter = TeamRoleToValueJacksonConverter.class)
    private TeamRole teamRole;
    private String teamName;

    private Boolean enabled;
    private Boolean isAcceptedByEmail;
    private Set<Authority> authorities;

    public UserInfoResponse(UserAcc userAcc) {
        this(userAcc, false, false);
    }

    public UserInfoResponse(UserAcc userAcc, boolean withAuthorities, boolean withTeamName) {
        this.userId = userAcc.getId();

        this.firstName = userAcc.getFirstName();
        this.lastName = userAcc.getLastName();
        this.nickName = userAcc.getNickName();
        this.registered = userAcc.getRegistered();

        this.profilePicUrl = ProfilePicHelper.getProfilePicUrl(userAcc, firstName, lastName, nickName);


        if (userAcc.getTeam() != null) {
            this.teamId = userAcc.getTeam().getId();
            if (withTeamName) {
                this.teamName = userAcc.getTeam().getName();
            }
        }
        this.teamRole = userAcc.getTeamRole();

        this.enabled = userAcc.getEnabled();
        this.isAcceptedByEmail = userAcc.getIsAcceptedByEmail();
        if (withAuthorities) {
            this.authorities = userAcc.getAuthorities();
        }
    }
}
