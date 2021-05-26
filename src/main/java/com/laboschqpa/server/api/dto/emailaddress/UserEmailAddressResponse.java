package com.laboschqpa.server.api.dto.emailaddress;

import com.laboschqpa.server.entity.account.UserEmailAddress;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserEmailAddressResponse {
    private Long id;
    private Long userId;
    private String email;

    public UserEmailAddressResponse(UserEmailAddress entity) {
        this.id = entity.getId();
        this.userId = entity.getUserAcc().getId();
        this.email = entity.getEmail();
    }
}
