package com.laboschqpa.server.api.controller;

import com.laboschqpa.server.config.userservice.CustomOauth2User;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.enums.apierrordescriptor.TeamMembershipApiError;
import com.laboschqpa.server.exceptions.apierrordescriptor.TeamMembershipException;

public class ControllerHelpers {

    public static Long getUserId(CustomOauth2User authenticationPrincipal) {
        return getUserAcc(authenticationPrincipal).getId();
    }

    public static UserAcc getUserAcc(CustomOauth2User authenticationPrincipal) {
        return authenticationPrincipal.getUserAccEntity();
    }

    public static Long getTeamId(CustomOauth2User authenticationPrincipal) {
        final UserAcc userAcc = authenticationPrincipal.getUserAccEntity();

        if (userAcc.getTeam() == null) {
            throw new TeamMembershipException(TeamMembershipApiError.YOU_ARE_NOT_IN_A_TEAM);
        }

        final Long teamId = userAcc.getTeam().getId();
        if (teamId != null && userAcc.getTeamRole().isMemberOrLeader()) {
            return teamId;
        }
        throw new TeamMembershipException(TeamMembershipApiError.YOU_ARE_NOT_IN_A_TEAM);
    }
}
