package com.laboschqpa.server.service.loginauthentication;

import com.laboschqpa.server.config.userservice.CustomOauth2User;
import com.laboschqpa.server.enums.auth.OAuth2ProviderRegistration;
import com.laboschqpa.server.exceptions.authentication.CorruptedContextAuthenticationException;
import com.laboschqpa.server.exceptions.authentication.InvalidLoginMethodAuthenticationException;
import com.laboschqpa.server.service.loginauthentication.handler.AddLoginMethodToExistingUserHandler;
import com.laboschqpa.server.service.loginauthentication.handler.LogInNewUserIntoSessionHandler;
import com.laboschqpa.server.service.oauth2.OAuth2Provider;
import com.laboschqpa.server.service.oauth2.OAuth2ProviderFactory;
import com.laboschqpa.server.service.oauth2.Oauth2UserProfileData;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@RequiredArgsConstructor
@Component
public class UserAccountLoginManagerImpl implements UserAccountLoginManager {
    private final OAuth2ProviderFactory oAuth2ProviderFactory;
    private final TransactionTemplate transactionTemplate;
    private final AddLoginMethodToExistingUserHandler addLoginMethodToExistingUserHandler;
    private final LogInNewUserIntoSessionHandler logInNewUserIntoSessionHandler;
    private final LogInHelpers logInHelpers;

    @Override
    public CustomOauth2User getExactUserFromOAuth2UserRequest(OAuth2UserRequest oAuth2UserRequest) {
        if (oAuth2UserRequest == null) {
            throw new CorruptedContextAuthenticationException("OAuth2UserRequest is null!");
        }

        return transactionTemplate.execute(s -> handleInTransaction(oAuth2UserRequest));
    }

    private CustomOauth2User handleInTransaction(OAuth2UserRequest oAuth2UserRequest) {
        ExplodedOAuth2UserRequestDto explodedRequest = explodeOAuth2UserRequest(oAuth2UserRequest);

        final CustomOauth2User oauth2User;
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            //No user is logged in in the session
            oauth2User = logInNewUserIntoSessionHandler.resolveUserAccountAndLogInIntoSession(explodedRequest);
        } else {
            //A user is probably logged in
            oauth2User = addLoginMethodToExistingUserHandler.handleOAuth2UserRequestWhenUserIsAlreadyLoggedIn(explodedRequest);
        }

        logInHelpers.updateProfilePicUrlIfNeeded(oauth2User.getUserAccEntity(), explodedRequest.getOauth2UserProfileData().getProfilePicUrl());
        return oauth2User;
    }

    private ExplodedOAuth2UserRequestDto explodeOAuth2UserRequest(OAuth2UserRequest oAuth2UserRequest) {
        final OAuth2ProviderRegistration providerRegistration = getOAuth2ProviderRegistrationFromOAuth2UserRequest(oAuth2UserRequest);
        final OAuth2Provider oAuth2ProviderService = oAuth2ProviderFactory.get(providerRegistration);
        final Oauth2UserProfileData oauth2UserProfileData = oAuth2ProviderService.extractDataFromOauth2UserRequest(oAuth2UserRequest);

        return new ExplodedOAuth2UserRequestDto(providerRegistration, oAuth2ProviderService, oauth2UserProfileData, oAuth2UserRequest);
    }

    private OAuth2ProviderRegistration getOAuth2ProviderRegistrationFromOAuth2UserRequest(OAuth2UserRequest oAuth2UserRequest) {
        String providerRegistrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        OAuth2ProviderRegistration providerRegistration = OAuth2ProviderRegistration.fromRegistrationKey(providerRegistrationId);
        if (providerRegistration == null) {
            throw new InvalidLoginMethodAuthenticationException("ProviderRegistration does not exist for key: " + providerRegistrationId);
        }
        return providerRegistration;
    }
}
