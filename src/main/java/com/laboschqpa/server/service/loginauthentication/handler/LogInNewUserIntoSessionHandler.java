package com.laboschqpa.server.service.loginauthentication.handler;

import com.laboschqpa.server.config.userservice.CustomOauth2User;
import com.laboschqpa.server.service.loginauthentication.ExplodedOAuth2UserRequestDto;

public interface LogInNewUserIntoSessionHandler {
    CustomOauth2User resolveUserAccountAndLogInIntoSession(ExplodedOAuth2UserRequestDto explodedRequest);
}
