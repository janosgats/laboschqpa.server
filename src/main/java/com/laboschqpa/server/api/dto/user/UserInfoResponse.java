package com.laboschqpa.server.api.dto.user;

import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.enums.auth.Authority;
import com.laboschqpa.server.util.ProfilePicHelper;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@AllArgsConstructor
@Data
public class UserInfoResponse {
    private Long userId;

    private String firstName;
    private String lastName;
    private String nickName;

    private String profilePicUrl;

    private Set<Authority> authorities;

    public UserInfoResponse(UserAcc userAcc) {
        this.userId = userAcc.getId();

        this.firstName = userAcc.getFirstName();
        this.lastName = userAcc.getLastName();
        this.nickName = userAcc.getNickName();

        this.profilePicUrl = ProfilePicHelper.getAvatarUrl(firstName, lastName, nickName);

        this.authorities = userAcc.getAuthorities();
    }
}
