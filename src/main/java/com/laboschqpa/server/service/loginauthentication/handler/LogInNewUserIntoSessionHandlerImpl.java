package com.laboschqpa.server.service.loginauthentication.handler;

import com.laboschqpa.server.config.userservice.CustomOauth2User;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.enums.auth.OAuth2ProviderRegistration;
import com.laboschqpa.server.exceptions.authentication.CannotFindExistingAccountToLogInAuthenticationException;
import com.laboschqpa.server.model.sessiondto.RegistrationSessionDto;
import com.laboschqpa.server.service.loginauthentication.ExplodedOAuth2UserRequestDto;
import com.laboschqpa.server.service.loginauthentication.LoginAuthenticationHelper;
import com.laboschqpa.server.service.loginauthentication.UserAccResolutionSource;
import com.laboschqpa.server.service.oauth2.OAuth2Provider;
import com.laboschqpa.server.service.oauth2.Oauth2UserProfileData;
import com.laboschqpa.server.service.useracc.UserAccResolutionResult;
import com.laboschqpa.server.service.useracc.UserAccResolverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Log4j2
@RequiredArgsConstructor
@Service
public class LogInNewUserIntoSessionHandlerImpl implements LogInNewUserIntoSessionHandler {
    private final UserAccResolverService userAccResolverService;
    private final LoginAuthenticationHelper loginAuthenticationHelper;

    @Override
    public CustomOauth2User resolveUserAccountAndLogInIntoSession(ExplodedOAuth2UserRequestDto explodedRequest) {
        final OAuth2ProviderRegistration providerRegistration = explodedRequest.getProviderRegistration();
        final OAuth2Provider oAuth2Provider = explodedRequest.getOAuth2Provider();
        final Oauth2UserProfileData oauth2UserProfileData = explodedRequest.getOauth2UserProfileData();

        final UserAccResolutionResult userAccResolutionResult = userAccResolverService.resolve(
                oAuth2Provider,
                oauth2UserProfileData.getEmailAddress(),
                oauth2UserProfileData.getExternalAccountDetail()
        );

        return handleAccountResolutionResult(
                userAccResolutionResult.getResolutionSource(),
                userAccResolutionResult.getUserAcc(),
                providerRegistration, oAuth2Provider, oauth2UserProfileData);
    }


    private CustomOauth2User handleAccountResolutionResult(final UserAccResolutionSource resolutionSource,
                                                           UserAcc userAccEntity,
                                                           final OAuth2ProviderRegistration providerRegistration,
                                                           final OAuth2Provider oAuth2ProviderService,
                                                           final Oauth2UserProfileData oauth2UserProfileData) {
        switch (resolutionSource) {
            case ByBoth:
                //Nothing to do here. Both E-mail and EAD is already saved for the user.
                break;
            case OnlyByExternalAccountDetail:
                //User is found by EAD but not by E-mail, so adding new E-mail.
                log.trace("User is found by EAD but not by E-mail, so adding new E-mail to the user if it presents.");
                loginAuthenticationHelper.saveNewEmailAddressForUserIfNotBlank(oauth2UserProfileData.getEmailAddress(), userAccEntity);
                break;
            case OnlyByEmail:
                //User is found by E-mail but not by EAD, so adding new EAD.
                log.trace("User is found by E-mail but not by EAD, so adding new EAD to the user.");
                oAuth2ProviderService.saveExternalAccountDetailForUserAcc(oauth2UserProfileData.getExternalAccountDetail(), userAccEntity);
                break;
            case ByNeither:
                log.trace("User is not found neither by E-mail nor by EAD.");
                putExtractedRegistrationDataIntoSession(oauth2UserProfileData, providerRegistration);
                throw new CannotFindExistingAccountToLogInAuthenticationException("Cannot find existing account");
            default:
                throw new IllegalStateException("This code part shouldn't have been reached!");
        }

        Objects.requireNonNull(userAccEntity, "userAccEntity shouldn't be null here!");

        CustomOauth2User customOauth2User = new CustomOauth2User(loginAuthenticationHelper.reloadUserAccFromDB(userAccEntity));
        return customOauth2User;
    }

    private void putExtractedRegistrationDataIntoSession(Oauth2UserProfileData oauth2UserProfileData, OAuth2ProviderRegistration providerRegistration) {
        RegistrationSessionDto dto = new RegistrationSessionDto();
        dto.setOauth2ProviderRegistrationKey(providerRegistration.getProviderRegistrationKey());
        dto.setOauth2ExternalAccountDetailString(oauth2UserProfileData.getExternalAccountDetail().getDetailString());

        dto.setEmailAddress(oauth2UserProfileData.getEmailAddress());
        dto.setFirstName(oauth2UserProfileData.getFirstName());
        dto.setLastName(oauth2UserProfileData.getLastName());
        dto.setNickName(oauth2UserProfileData.getNickName());

        dto.writeToCurrentSession();
    }
}
