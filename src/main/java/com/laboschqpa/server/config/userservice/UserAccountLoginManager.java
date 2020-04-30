package com.laboschqpa.server.config.userservice;

import com.laboschqpa.server.config.helper.EnumBasedAuthority;
import com.laboschqpa.server.entity.RegistrationRequest;
import com.laboschqpa.server.entity.account.externalaccountdetail.ExternalAccountDetail;
import com.laboschqpa.server.enums.auth.Authority;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.account.UserEmailAddress;
import com.laboschqpa.server.enums.RegistrationRequestPhase;
import com.laboschqpa.server.enums.auth.OAuth2ProviderRegistrations;
import com.laboschqpa.server.exceptions.authentication.*;
import com.laboschqpa.server.model.sessiondto.JoinFlowSessionDto;
import com.laboschqpa.server.repo.*;
import com.laboschqpa.server.service.oauth2.AbstractOAuth2ProviderService;
import com.laboschqpa.server.service.oauth2.OAuth2ProviderServiceSelector;
import com.laboschqpa.server.service.oauth2.ExtractedOAuth2UserRequestDataDto;
import com.laboschqpa.server.util.SessionHelper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.laboschqpa.server.config.userservice.UserAccountResolvingResult.*;

@RequiredArgsConstructor
@Component
public class UserAccountLoginManager {
    private static final Logger logger = LoggerFactory.getLogger(UserAccountLoginManager.class);

    private final UserEmailAddressRepository userEmailAddressRepository;
    private final UserAccRepository userAccRepository;
    private final RegistrationRequestRepository registrationRequestRepository;
    private final OAuth2ProviderServiceSelector oAuth2ProviderServiceSelector;

    public CustomOauth2User getExactUser(OAuth2UserRequest oAuth2UserRequest) {
        if (oAuth2UserRequest == null) {
            throw new CorruptedContextAuthenticationException("OAuth2UserRequest is null!");
        }
        ExplodedOAuth2UserRequestDto explodedRequest = explodeOAuth2UserRequest(oAuth2UserRequest);

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return logInNewUserIntoSession(
                    explodedRequest.getProviderRegistration(),
                    explodedRequest.getOAuth2ProviderService(),
                    explodedRequest.getExtractedOAuth2UserRequestDataDto());
        } else {
            return handleRequestWhenUserIsPossiblyAlreadyLoggedIn(explodedRequest);
        }
    }

    private CustomOauth2User handleRequestWhenUserIsPossiblyAlreadyLoggedIn(ExplodedOAuth2UserRequestDto explodedRequest) {
        UserAcc userAccEntity = assertAndGetExistingUserAccFromSessionToAddNewLoginMethod();
        return addLoginMethodToExistingUserAccount(userAccEntity, explodedRequest);
    }

    private UserAcc assertAndGetExistingUserAccFromSessionToAddNewLoginMethod() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomOauth2User) {
            CustomOauth2User customOauth2User = (CustomOauth2User) principal;
            assertUserAccIsEnabled(customOauth2User.getUserAccEntity());
            return customOauth2User.getUserAccEntity();
        } else {
            SessionHelper.invalidateCurrentSession();
            throw new CorruptedContextAuthenticationException("Authentication principal is not instance of CustomOauth2User");
        }
    }

    private CustomOauth2User addLoginMethodToExistingUserAccount(UserAcc existingUserAcc, ExplodedOAuth2UserRequestDto explodedRequest) {
        final AbstractOAuth2ProviderService oAuth2ProviderService = explodedRequest.getOAuth2ProviderService();
        final ExtractedOAuth2UserRequestDataDto extractedOAuth2UserRequestDataDto = explodedRequest.getExtractedOAuth2UserRequestDataDto();

        final boolean emailAddressWasPresentInOauth2Response = extractedOAuth2UserRequestDataDto.getEmailAddress() != null;

        final Boolean doesEmailAddressAlreadyExist;
        if (emailAddressWasPresentInOauth2Response) {
            doesEmailAddressAlreadyExist = userEmailAddressRepository
                    .findByEmail(extractedOAuth2UserRequestDataDto.getEmailAddress()).isPresent();
        } else {
            doesEmailAddressAlreadyExist = null;
        }

        final boolean doesExternalAccountDetailAlreadyExist = oAuth2ProviderService
                .loadUserAccFromDbByExternalAccountDetail(extractedOAuth2UserRequestDataDto.getExternalAccountDetail()) != null;

        logger.info("Adding login method to existing user ({}). Digest: providerRegistration: {}, emailAddressWasPresentInOauth2Response: {}," +
                        " doesEmailAddressAlreadyExist: {}, doesExternalAccountDetailAlreadyExist: {}", existingUserAcc.getId(),
                explodedRequest.getProviderRegistration(), emailAddressWasPresentInOauth2Response, doesEmailAddressAlreadyExist, doesExternalAccountDetailAlreadyExist);

        if (!doesExternalAccountDetailAlreadyExist) {
            explodedRequest.getOAuth2ProviderService().saveExternalAccountDetailForUserAcc(extractedOAuth2UserRequestDataDto.getExternalAccountDetail(), existingUserAcc);
        }

        if (emailAddressWasPresentInOauth2Response && !doesEmailAddressAlreadyExist) {
            saveNewEmailAddressForUserIfEmailIsNotNull(extractedOAuth2UserRequestDataDto.getEmailAddress(), existingUserAcc);
        }

        return new CustomOauth2User(reloadUserAccFromDB(existingUserAcc));
    }

    private ExplodedOAuth2UserRequestDto explodeOAuth2UserRequest(OAuth2UserRequest oAuth2UserRequest) {
        final OAuth2ProviderRegistrations providerRegistration = getOAuth2ProviderRegistrationFromOAuth2UserRequest(oAuth2UserRequest);
        final AbstractOAuth2ProviderService oAuth2ProviderService = oAuth2ProviderServiceSelector.getProviderService(providerRegistration);
        final ExtractedOAuth2UserRequestDataDto extractedOAuth2UserRequestDataDto = oAuth2ProviderService.extractDataFromOauth2UserRequest(oAuth2UserRequest);

        return new ExplodedOAuth2UserRequestDto(providerRegistration, oAuth2ProviderService, extractedOAuth2UserRequestDataDto, oAuth2UserRequest);
    }

    private CustomOauth2User logInNewUserIntoSession(final OAuth2ProviderRegistrations providerRegistration,
                                                     final AbstractOAuth2ProviderService oAuth2ProviderService,
                                                     final ExtractedOAuth2UserRequestDataDto extractedOAuth2UserRequestDataDto) {
        final boolean emailAddressWasPresentInOauth2Response = extractedOAuth2UserRequestDataDto.getEmailAddress() != null;

        UserAcc userAccEntity = oAuth2ProviderService.loadUserAccFromDbByExternalAccountDetail(extractedOAuth2UserRequestDataDto.getExternalAccountDetail());
        final boolean userFoundByExternalAccountDetail = userAccEntity != null;

        final UserEmailAddress userEmailAddressFromRequestTriedToLoadFromDB;
        if (emailAddressWasPresentInOauth2Response) {
            userEmailAddressFromRequestTriedToLoadFromDB = userEmailAddressRepository
                    .findByEmail(extractedOAuth2UserRequestDataDto.getEmailAddress()).orElse(null);
        } else {
            userEmailAddressFromRequestTriedToLoadFromDB = null;
        }

        final boolean userFoundByEmailAddress = userEmailAddressFromRequestTriedToLoadFromDB != null;

        if (userFoundByEmailAddress) {
            userAccEntity = userEmailAddressFromRequestTriedToLoadFromDB.getUserAcc();
        }

        if (userFoundByExternalAccountDetail || userFoundByEmailAddress) {
            assertUserAccIsEnabled(userAccEntity);
        }

        final UserAccountResolvingResult accountResolvingResult = determineUserAccountResolvingResult(userFoundByExternalAccountDetail, userFoundByEmailAddress);

        logger.info("Logging in user ({}) into session. Digest: providerRegistration: {}, emailAddressWasPresentInOauth2Response: {}, accountResolvingResult: {}",
                userAccEntity != null ? userAccEntity.getId() : "null", providerRegistration, emailAddressWasPresentInOauth2Response, accountResolvingResult);

        switch (accountResolvingResult) {
            case ByBoth:
                logger.trace("User is found both by EAD and by E-mail.");
                if (!userAccEntity.getId().equals(userEmailAddressFromRequestTriedToLoadFromDB.getUserAcc().getId())) {
                    logger.info("User is found both by EAD and by E-mail, but they are NOT the same user.");
                    //If the user is found by EAD and also by Email, than the two users HAVE TO BE THE SAME!
                    throw new EmailGotFromOAuth2ResponseBelongsToAnOtherAccountAuthenticationException(
                            "E-mail got from OAuth2 response is saved in the system as a different User's e-mail address: "
                                    + userEmailAddressFromRequestTriedToLoadFromDB.getEmail()
                                    + " Please contact support!");
                }
                break;
            case OnlyByExternalAccountDetail:
                //User is found by EAD but not by E-mail, so adding new E-mail.
                logger.trace("User is found by EAD but not by E-mail, so adding new E-mail to the user if it presents.");
                saveNewEmailAddressForUserIfEmailIsNotNull(extractedOAuth2UserRequestDataDto.getEmailAddress(), userAccEntity);
                break;
            case OnlyByEmail:
                //User is found by E-mail but not by EAD, so adding new EAD.
                logger.trace("User is found by E-mail but not by EAD, so adding new EAD to the user.");
                oAuth2ProviderService.saveExternalAccountDetailForUserAcc(extractedOAuth2UserRequestDataDto.getExternalAccountDetail(), userAccEntity);
                break;
            case ByNeither:
                logger.trace("User is not found neither by E-mail nor by EAD.");
                userAccEntity = attemptToRegisterNewUser(extractedOAuth2UserRequestDataDto.getExternalAccountDetail(), extractedOAuth2UserRequestDataDto.getEmailAddress(), oAuth2ProviderService);
                logger.info("Registered new user ({}) from: {}", userAccEntity.getId(), providerRegistration);
                break;
            default:
                throw new IllegalStateException("This code part shouldn't have been reached!");
        }

        Objects.requireNonNull(userAccEntity, "userAccEntity shouldn't be null here!");
        return new CustomOauth2User(reloadUserAccFromDB(userAccEntity));
    }

    private void saveNewEmailAddressForUserIfEmailIsNotNull(String emailAddress, UserAcc userAccEntity) {
        if (emailAddress != null) {
            UserEmailAddress newUserEmailAddress = new UserEmailAddress();
            newUserEmailAddress.setEmail(emailAddress);
            newUserEmailAddress.setUserAcc(userAccEntity);
            userEmailAddressRepository.save(newUserEmailAddress);
        }
    }

    private UserAccountResolvingResult determineUserAccountResolvingResult(boolean userFoundByExternalAccountDetail, boolean userFoundByEmailAddress) {
        if (userFoundByExternalAccountDetail && userFoundByEmailAddress) {
            return ByBoth;
        } else if (userFoundByExternalAccountDetail && !userFoundByEmailAddress) {
            return OnlyByExternalAccountDetail;
        } else if (!userFoundByExternalAccountDetail && userFoundByEmailAddress) {
            return OnlyByEmail;
        } else if (!userFoundByExternalAccountDetail && !userFoundByEmailAddress) {
            return ByNeither;
        } else {
            throw new IllegalStateException("This code part shouldn't have been reached!");
        }
    }

    private void assertUserAccIsEnabled(UserAcc userAccEntity) {
        if (!userAccEntity.getEnabled()) {
            throw new UserAccountIsDisabledAuthenticationException("The user account is disabled!.");
        }
    }

    private UserAcc attemptToRegisterNewUser(ExternalAccountDetail externalAccountDetail, String emailAddress, AbstractOAuth2ProviderService oAuth2ProviderService) {
        JoinFlowSessionDto joinFlowSessionDto = JoinFlowSessionDto.readFromCurrentSession();
        if (joinFlowSessionDto != null && joinFlowSessionDto.getRegistrationRequestId() != null) {
            RegistrationRequest registrationRequest = getValidRegistrationRequestById(joinFlowSessionDto.getRegistrationRequestId());

            UserAcc registeredUserAccEntity = initNewUserAccEntity();
            oAuth2ProviderService.saveExternalAccountDetailForUserAcc(externalAccountDetail, registeredUserAccEntity);

            saveNewEmailAddressForUserIfEmailIsNotNull(emailAddress, registeredUserAccEntity);
            //Saving both mails to the user if they are different
            if (!registrationRequest.getEmail().equals(emailAddress)) {
                saveNewEmailAddressForUserIfEmailIsNotNull(registrationRequest.getEmail(), registeredUserAccEntity);
            }

            joinFlowSessionDto.setRegistrationRequestId(null);
            joinFlowSessionDto.writeToCurrentSession();
            registrationRequest.setPhase(RegistrationRequestPhase.REGISTERED);
            registrationRequestRepository.save(registrationRequest);

            return registeredUserAccEntity;
        } else {
            throw new CannotFindExistingAccountAndNoRegistrationSessionDataIsSetAuthenticationException("Cannot find existing user account or e-mail that this login can be merged to!");
        }
    }

    private RegistrationRequest getValidRegistrationRequestById(long registrationRequestId) {
        Optional<RegistrationRequest> registrationRequestOptional = registrationRequestRepository.findById(registrationRequestId);
        if (registrationRequestOptional.isEmpty() || registrationRequestOptional.get().getPhase() != RegistrationRequestPhase.EMAIL_VERIFIED) {
            throw new RegistrationRequestReferredBySessionDataIsInvalidAuthenticationException("Cannot found existing account neither registration request with verified e-mail!");
        }
        return registrationRequestOptional.get();
    }

    private UserAcc initNewUserAccEntity() {
        UserAcc newUserAcc = new UserAcc();

        newUserAcc.setEnabled(true);
        newUserAcc.setAuthorities_FromEnumBasedAuthority(Set.of(new EnumBasedAuthority(Authority.User)));

        userAccRepository.save(newUserAcc);
        return newUserAcc;
    }

    private OAuth2ProviderRegistrations getOAuth2ProviderRegistrationFromOAuth2UserRequest(OAuth2UserRequest oAuth2UserRequest) {
        String providerRegistrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        OAuth2ProviderRegistrations providerRegistration = OAuth2ProviderRegistrations.fromProviderRegistrationKey(providerRegistrationId);
        if (providerRegistration == null) {
            throw new InvalidLoginMethodAuthenticationException("ProviderRegistration does not exist for key: " + providerRegistrationId);
        }
        return providerRegistration;
    }

    private UserAcc reloadUserAccFromDB(UserAcc previousUserAcc) {
        Optional<UserAcc> userAccOptional = userAccRepository.findById(previousUserAcc.getId());
        if (userAccOptional.isEmpty()) {
            throw new CorruptedContextAuthenticationException("Cannot reload user account. Please contact support about this error message!");
        }

        UserAcc reloadedUserAcc = userAccOptional.get();
        assertUserAccIsEnabled(reloadedUserAcc);
        return reloadedUserAcc;
    }
}
