package com.laboschqpa.server.api.dto.user;

import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.util.ProfilePicHelper;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserInfoResponse {
    private Long userId;

    private String firstName;
    private String lastName;
    private String nickName;

    private String profilePicUrl;

    public UserInfoResponse(UserAcc userAcc) {
        this.userId = userAcc.getId();
        this.firstName = userAcc.getFirstName();
        this.lastName = userAcc.getLastName();
        this.nickName = userAcc.getNickName();

        this.profilePicUrl = ProfilePicHelper.getAvatarUrl(firstName, lastName, nickName);
    }
}
