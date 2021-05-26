package com.laboschqpa.server.service.useracc;

import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.service.loginauthentication.UserAccResolutionSource;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserAccResolutionResult {
    private final UserAccResolutionSource resolutionSource;
    private final UserAcc userAcc;
}
