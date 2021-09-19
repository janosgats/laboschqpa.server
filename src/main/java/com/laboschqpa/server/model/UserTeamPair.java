package com.laboschqpa.server.model;

import com.laboschqpa.server.entity.account.UserAcc;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTeamPair {
    private Long userId;
    private Long teamId;

    public static UserTeamPair of(UserAcc userAcc) {
        final UserTeamPair userTeamPair = new UserTeamPair();
        userTeamPair.setUserId(userAcc.getId());
        if (userAcc.getTeam() != null) {
            userTeamPair.setTeamId(userAcc.getTeam().getId());
        }
        return userTeamPair;
    }
}
