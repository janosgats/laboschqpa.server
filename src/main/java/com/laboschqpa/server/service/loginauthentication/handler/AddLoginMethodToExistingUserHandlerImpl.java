package com.laboschqpa.server.service.loginauthentication.handler;

import com.laboschqpa.server.config.userservice.CustomOauth2User;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.account.UserEmailAddress;
import com.laboschqpa.server.entity.account.externalaccountdetail.ExternalAccountDetail;
import com.laboschqpa.server.enums.auth.OAuth2ProviderRegistration;
import com.laboschqpa.server.exceptions.authentication.CorruptedContextAuthenticationException;
import com.laboschqpa.server.exceptions.authentication.EmailBelongsToAnOtherAccountAuthenticationException;
import com.laboschqpa.server.exceptions.authentication.ExternalAccountGotFromOAuth2ResponseBelongsToAnOtherAccountAuthenticationException;
import com.laboschqpa.server.repo.UserEmailAddressRepository;
import com.laboschqpa.server.service.loginauthentication.ExplodedOAuth2UserRequestDto;
import com.laboschqpa.server.service.loginauthentication.LoginAuthenticationHelper;
import com.laboschqpa.server.service.oauth2.OAuth2Provider;
import com.laboschqpa.server.service.oauth2.Oauth2UserProfileData;
import com.laboschqpa.server.util.SessionHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
@Service
public class AddLoginMethodToExistingUserHandlerImpl implements AddLoginMethodToExistingUserHandler {
    private final UserEmailAddressRepository userEmailAddressRepository;
    private final LoginAuthenticationHelper loginAuthenticationHelper;

    @Override
    public CustomOauth2User handleOAuth2UserRequestWhenUserIsAlreadyLoggedIn(ExplodedOAuth2UserRequestDto explodedRequest) {
        UserAcc userAccEntity = assertAndGetExistingUserAccFromSessionToAddNewLoginMethodTo();
        return addLoginMethodToExistingUserAccount(userAccEntity, explodedRequest);
    }

    private UserAcc assertAndGetExistingUserAccFromSessionToAddNewLoginMethodTo() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomOauth2User) {
            CustomOauth2User customOauth2User = (CustomOauth2User) principal;
            loginAuthenticationHelper.assertUserAccIsEnabled(customOauth2User.getUserAccEntity());
            return customOauth2User.getUserAccEntity();
        } else {
            SessionHelper.invalidateCurrentSession();
            throw new CorruptedContextAuthenticationException("Authentication principal is not instance of CustomOauth2User");
        }
    }

    private CustomOauth2User addLoginMethodToExistingUserAccount(UserAcc existingUserAcc, ExplodedOAuth2UserRequestDto explodedRequest) {
        final OAuth2ProviderRegistration providerRegistration = explodedRequest.getProviderRegistration();
        final OAuth2Provider oAuth2ProviderService = explodedRequest.getOAuth2Provider();
        final Oauth2UserProfileData oauth2UserProfileData = explodedRequest.getOauth2UserProfileData();

        //Email
        final boolean emailAddressWasPresentInOauth2Response = oauth2UserProfileData.getEmailAddress() != null;
        final Boolean doesEmailAddressAlreadyExist = assertAndDetermineIfEmailAddressAlreadyExists(
                existingUserAcc,
                emailAddressWasPresentInOauth2Response,
                oauth2UserProfileData.getEmailAddress());

        //ExternalAccountDetail
        final boolean doesExternalAccountDetailAlreadyExist = assertAndDetermineIfExternalAccountDetailAlreadyExists(
                existingUserAcc,
                providerRegistration,
                oAuth2ProviderService,
                oauth2UserProfileData.getExternalAccountDetail());

        log.info("Adding login method to existing user ({}). Digest: providerRegistration: {}, emailAddressWasPresentInOauth2Response: {}," +
                        " doesEmailAddressAlreadyExist: {}, doesExternalAccountDetailAlreadyExist: {}", existingUserAcc.getId(),
                providerRegistration, emailAddressWasPresentInOauth2Response, doesEmailAddressAlreadyExist, doesExternalAccountDetailAlreadyExist);

        if (!doesExternalAccountDetailAlreadyExist) {
            oAuth2ProviderService.saveExternalAccountDetailForUserAcc(oauth2UserProfileData.getExternalAccountDetail(), existingUserAcc);
        }

        if (emailAddressWasPresentInOauth2Response && !doesEmailAddressAlreadyExist) {
            loginAuthenticationHelper.saveNewEmailAddressForUserIfNotBlank(oauth2UserProfileData.getEmailAddress(), existingUserAcc);
        }

        return new CustomOauth2User(loginAuthenticationHelper.reloadUserAccFromDB(existingUserAcc));
    }

    private Boolean assertAndDetermineIfEmailAddressAlreadyExists(UserAcc existingUserAcc,
                                                                  boolean emailAddressWasPresentInOauth2Response,
                                                                  String emailAddressFromOauth2Response) {
        if (emailAddressWasPresentInOauth2Response) {
            final Optional<UserEmailAddress> userEmailAddressOptional = userEmailAddressRepository.findByEmail(emailAddressFromOauth2Response);
            final boolean doesEmailAddressAlreadyExist = userEmailAddressOptional.isPresent();

            if (doesEmailAddressAlreadyExist) {
                assertExistingUserAccIsTheSameAsTheOneThatIsStoredForTheEmailAddress(existingUserAcc, userEmailAddressOptional.get());
            }
            return doesEmailAddressAlreadyExist;
        } else {
            return null;
        }
    }

    private Boolean assertAndDetermineIfExternalAccountDetailAlreadyExists(UserAcc existingUserAcc,
                                                                           OAuth2ProviderRegistration providerRegistration,
                                                                           OAuth2Provider oAuth2ProviderService,
                                                                           ExternalAccountDetail externalAccountDetail) {
        final UserAcc userAccLoadedByExternalAccountDetail
                = oAuth2ProviderService.loadUserAccFromDbByExternalAccountDetail(externalAccountDetail);
        final boolean doesExternalAccountDetailAlreadyExist = userAccLoadedByExternalAccountDetail != null;

        if (doesExternalAccountDetailAlreadyExist) {
            assertExistingUserAccIsTheSameAsTheOneThatIsStoredForTheExternalAccountDetail(existingUserAcc, userAccLoadedByExternalAccountDetail, providerRegistration);
        }
        return doesExternalAccountDetailAlreadyExist;
    }

    private void assertExistingUserAccIsTheSameAsTheOneThatIsStoredForTheEmailAddress(UserAcc existingUserAcc,
                                                                                      UserEmailAddress userEmailAddress) {
        if (!existingUserAcc.getId().equals(userEmailAddress.getUserAcc().getId())) {
            log.info("During addLoginMethod to existing user: User is found by E-mail, but it's NOT the same as the initiator user.");
            //If the user is found by EAD or by Email, then the two users HAVE TO BE THE SAME!
            throw new EmailBelongsToAnOtherAccountAuthenticationException(
                    "E-mail got from OAuth2 response is saved in the system as a different User's e-mail address: "
                            + userEmailAddress.getEmail()
                            + ". Please contact support!");
        }
    }

    private void assertExistingUserAccIsTheSameAsTheOneThatIsStoredForTheExternalAccountDetail(UserAcc existingUserAcc,
                                                                                               UserAcc userAccLoadedByExternalAccountDetail,
                                                                                               OAuth2ProviderRegistration providerRegistration) {
        if (!existingUserAcc.getId().equals(userAccLoadedByExternalAccountDetail.getId())) {
            log.info("During addLoginMethod to existing user: User is found by EAD, but it's NOT the same as the initiator user.");
            //If the user is found by EAD, then the two users HAVE TO BE THE SAME!
            throw new ExternalAccountGotFromOAuth2ResponseBelongsToAnOtherAccountAuthenticationException(
                    "EAD got from OAuth2 response is saved in the system as a different User's EAD. "
                            + "Provider: " + providerRegistration
                            + ". Please contact support!");
        }
    }
}
