package com.laboschqpa.server.service.loginauthentication.handler;

import com.laboschqpa.server.config.userservice.CustomOauth2User;
import com.laboschqpa.server.service.loginauthentication.ExplodedOAuth2UserRequestDto;

public interface AddLoginMethodToExistingUserHandler {
    CustomOauth2User handleOAuth2UserRequestWhenUserIsAlreadyLoggedIn(ExplodedOAuth2UserRequestDto explodedRequest);
}
