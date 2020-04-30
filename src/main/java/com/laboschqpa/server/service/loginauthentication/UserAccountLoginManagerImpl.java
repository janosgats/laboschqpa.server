package com.laboschqpa.server.service.loginauthentication;

import com.laboschqpa.server.config.userservice.CustomOauth2User;
import com.laboschqpa.server.enums.auth.OAuth2ProviderRegistrations;
import com.laboschqpa.server.exceptions.authentication.CorruptedContextAuthenticationException;
import com.laboschqpa.server.exceptions.authentication.InvalidLoginMethodAuthenticationException;
import com.laboschqpa.server.service.loginauthentication.handler.AddLoginMethodToExistingUserHandler;
import com.laboschqpa.server.service.loginauthentication.handler.LogInNewUserIntoSessionHandler;
import com.laboschqpa.server.service.oauth2.AbstractOAuth2ProviderService;
import com.laboschqpa.server.service.oauth2.ExtractedOAuth2UserRequestDataDto;
import com.laboschqpa.server.service.oauth2.OAuth2ProviderServiceSelector;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@RequiredArgsConstructor
@Component
public class UserAccountLoginManagerImpl implements UserAccountLoginManager {

    private final OAuth2ProviderServiceSelector oAuth2ProviderServiceSelector;
    private final TransactionTemplate transactionTemplate;
    private final AddLoginMethodToExistingUserHandler addLoginMethodToExistingUserHandler;
    private final LogInNewUserIntoSessionHandler logInNewUserIntoSessionHandler;

    @Override
    public CustomOauth2User getExactUserFromOAuth2UserRequest(OAuth2UserRequest oAuth2UserRequest) {
        if (oAuth2UserRequest == null) {
            throw new CorruptedContextAuthenticationException("OAuth2UserRequest is null!");
        }

        return transactionTemplate.execute(s -> handleInTransaction(oAuth2UserRequest));
    }

    private CustomOauth2User handleInTransaction(OAuth2UserRequest oAuth2UserRequest) {
        ExplodedOAuth2UserRequestDto explodedRequest = explodeOAuth2UserRequest(oAuth2UserRequest);

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            //No user is logged in in the session
            return logInNewUserIntoSessionHandler.resolveUserAccountAndLogInIntoSession(explodedRequest);
        } else {
            //A user is probably logged in
            return addLoginMethodToExistingUserHandler.handleOAuth2UserRequestWhenUserIsAlreadyLoggedIn(explodedRequest);
        }
    }

    private ExplodedOAuth2UserRequestDto explodeOAuth2UserRequest(OAuth2UserRequest oAuth2UserRequest) {
        final OAuth2ProviderRegistrations providerRegistration = getOAuth2ProviderRegistrationFromOAuth2UserRequest(oAuth2UserRequest);
        final AbstractOAuth2ProviderService oAuth2ProviderService = oAuth2ProviderServiceSelector.getProviderService(providerRegistration);
        final ExtractedOAuth2UserRequestDataDto extractedOAuth2UserRequestDataDto = oAuth2ProviderService.extractDataFromOauth2UserRequest(oAuth2UserRequest);

        return new ExplodedOAuth2UserRequestDto(providerRegistration, oAuth2ProviderService, extractedOAuth2UserRequestDataDto, oAuth2UserRequest);
    }

    private OAuth2ProviderRegistrations getOAuth2ProviderRegistrationFromOAuth2UserRequest(OAuth2UserRequest oAuth2UserRequest) {
        String providerRegistrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        OAuth2ProviderRegistrations providerRegistration = OAuth2ProviderRegistrations.fromProviderRegistrationKey(providerRegistrationId);
        if (providerRegistration == null) {
            throw new InvalidLoginMethodAuthenticationException("ProviderRegistration does not exist for key: " + providerRegistrationId);
        }
        return providerRegistration;
    }
}
