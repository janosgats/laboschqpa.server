package com.laboschqpa.server.service.loginauthentication.handler;

import com.laboschqpa.server.config.userservice.CustomOauth2User;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.account.UserEmailAddress;
import com.laboschqpa.server.enums.auth.OAuth2ProviderRegistrations;
import com.laboschqpa.server.exceptions.authentication.EmailGotFromOAuth2ResponseBelongsToAnOtherAccountAuthenticationException;
import com.laboschqpa.server.service.loginauthentication.ExplodedOAuth2UserRequestDto;
import com.laboschqpa.server.service.oauth2.AbstractOAuth2ProviderService;
import com.laboschqpa.server.service.oauth2.ExtractedOAuth2UserRequestDataDto;

import java.util.Objects;

public interface LogInNewUserIntoSessionHandler {
    CustomOauth2User resolveUserAccountAndLogInIntoSession(ExplodedOAuth2UserRequestDto explodedRequest);
}
