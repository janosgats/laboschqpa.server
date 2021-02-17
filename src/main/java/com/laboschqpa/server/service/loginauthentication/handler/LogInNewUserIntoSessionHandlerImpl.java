package com.laboschqpa.server.service.loginauthentication.handler;

import com.laboschqpa.server.config.helper.EnumBasedAuthority;
import com.laboschqpa.server.config.userservice.CustomOauth2User;
import com.laboschqpa.server.entity.RegistrationRequest;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.account.UserEmailAddress;
import com.laboschqpa.server.enums.RegistrationRequestPhase;
import com.laboschqpa.server.enums.auth.Authority;
import com.laboschqpa.server.enums.auth.OAuth2ProviderRegistrations;
import com.laboschqpa.server.exceptions.authentication.CannotFindExistingAccountAndNoRegistrationSessionDataIsSetAuthenticationException;
import com.laboschqpa.server.exceptions.authentication.EmailGotFromOAuth2ResponseBelongsToAnOtherAccountAuthenticationException;
import com.laboschqpa.server.exceptions.authentication.RegistrationRequestReferredBySessionDataIsInvalidAuthenticationException;
import com.laboschqpa.server.model.sessiondto.JoinFlowSessionDto;
import com.laboschqpa.server.repo.RegistrationRequestRepository;
import com.laboschqpa.server.repo.UserAccRepository;
import com.laboschqpa.server.repo.UserEmailAddressRepository;
import com.laboschqpa.server.service.loginauthentication.ExplodedOAuth2UserRequestDto;
import com.laboschqpa.server.service.loginauthentication.LoginAuthenticationHelper;
import com.laboschqpa.server.service.loginauthentication.UserAccountResolutionSource;
import com.laboschqpa.server.service.oauth2.AbstractOAuth2ProviderService;
import com.laboschqpa.server.service.oauth2.ExtractedOAuth2UserRequestDataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.laboschqpa.server.service.loginauthentication.UserAccountResolutionSource.*;

@Log4j2
@RequiredArgsConstructor
@Service
public class LogInNewUserIntoSessionHandlerImpl implements LogInNewUserIntoSessionHandler {
    private final UserEmailAddressRepository userEmailAddressRepository;
    private final UserAccRepository userAccRepository;
    private final RegistrationRequestRepository registrationRequestRepository;
    private final LoginAuthenticationHelper loginAuthenticationHelper;

    @Override
    public CustomOauth2User resolveUserAccountAndLogInIntoSession(ExplodedOAuth2UserRequestDto explodedRequest) {
        final OAuth2ProviderRegistrations providerRegistration = explodedRequest.getProviderRegistration();
        final AbstractOAuth2ProviderService oAuth2ProviderService = explodedRequest.getOAuth2ProviderService();
        final ExtractedOAuth2UserRequestDataDto extractedOAuth2UserRequestDataDto = explodedRequest.getExtractedOAuth2UserRequestDataDto();

        final Pair<UserAccountResolutionSource, UserAcc> accountResolutionResult
                = resolveUserAccount(providerRegistration, oAuth2ProviderService, extractedOAuth2UserRequestDataDto);

        return handleAccountResolutionResult(accountResolutionResult.getLeft(), accountResolutionResult.getRight(),
                providerRegistration, oAuth2ProviderService, extractedOAuth2UserRequestDataDto);
    }

    private Pair<UserAccountResolutionSource, UserAcc> resolveUserAccount(OAuth2ProviderRegistrations providerRegistration,
                                                                          AbstractOAuth2ProviderService oAuth2ProviderService,
                                                                          ExtractedOAuth2UserRequestDataDto extractedOAuth2UserRequestDataDto) {
        final boolean emailAddressWasPresentInOauth2Response = extractedOAuth2UserRequestDataDto.getEmailAddress() != null;

        final UserEmailAddress userEmailAddressFromRequestTriedToLoadFromDB;
        if (emailAddressWasPresentInOauth2Response) {
            userEmailAddressFromRequestTriedToLoadFromDB = userEmailAddressRepository
                    .findByEmail(extractedOAuth2UserRequestDataDto.getEmailAddress()).orElse(null);
        } else {
            userEmailAddressFromRequestTriedToLoadFromDB = null;
        }

        final boolean userFoundByEmailAddress = userEmailAddressFromRequestTriedToLoadFromDB != null;

        final UserAcc userAccEntityByEmail;
        if (userFoundByEmailAddress) {
            userAccEntityByEmail = userEmailAddressFromRequestTriedToLoadFromDB.getUserAcc();
        } else {
            userAccEntityByEmail = null;
        }

        final UserAcc userAccEntityByEAD = oAuth2ProviderService.loadUserAccFromDbByExternalAccountDetail(extractedOAuth2UserRequestDataDto.getExternalAccountDetail());
        final boolean userFoundByExternalAccountDetail = userAccEntityByEAD != null;

        final UserAccountResolutionSource accountResolutionSource = determineUserAccountResolvingResult(userFoundByExternalAccountDetail, userFoundByEmailAddress);

        final UserAcc resolvedUserAccEntity;
        switch (accountResolutionSource) {
            case ByBoth:
                log.trace("User is found both by EAD and by E-mail.");
                if (!userAccEntityByEAD.getId().equals(userAccEntityByEmail.getId())) {
                    log.info("During login user into session: User is found both by EAD and by E-mail, but they are NOT the same user.");
                    //If the user is found by EAD and also by Email, than the two users HAVE TO BE THE SAME!
                    throw new EmailGotFromOAuth2ResponseBelongsToAnOtherAccountAuthenticationException(
                            "E-mail got from OAuth2 response is saved in the system as a different User's e-mail address: "
                                    + userEmailAddressFromRequestTriedToLoadFromDB.getEmail()
                                    + ". Please contact support!");
                }
                resolvedUserAccEntity = userAccEntityByEAD;
                break;
            case OnlyByExternalAccountDetail:
                resolvedUserAccEntity = userAccEntityByEAD;
                break;
            case OnlyByEmail:
                resolvedUserAccEntity = userAccEntityByEmail;
                break;
            case ByNeither:
                resolvedUserAccEntity = null;
                break;
            default:
                throw new IllegalStateException("This code part shouldn't have been reached!");
        }

        if (resolvedUserAccEntity != null) {
            loginAuthenticationHelper.assertUserAccIsEnabled(resolvedUserAccEntity);
        }

        log.info("User Account Resolved. Digest: userId: {} providerRegistration: {}, emailAddressWasPresentInOauth2Response: {}, accountResolutionSource: {}",
                resolvedUserAccEntity != null ? resolvedUserAccEntity.getId() : "null",
                providerRegistration, emailAddressWasPresentInOauth2Response, accountResolutionSource);

        return Pair.of(accountResolutionSource, resolvedUserAccEntity);
    }

    private CustomOauth2User handleAccountResolutionResult(final UserAccountResolutionSource accountResolutionResult,
                                                           UserAcc userAccEntity,
                                                           final OAuth2ProviderRegistrations providerRegistration,
                                                           final AbstractOAuth2ProviderService oAuth2ProviderService,
                                                           final ExtractedOAuth2UserRequestDataDto extractedOAuth2UserRequestDataDto) {
        switch (accountResolutionResult) {
            case ByBoth:
                //Nothing to do here. Both E-mail and EAD is already saved for the user.
                break;
            case OnlyByExternalAccountDetail:
                //User is found by EAD but not by E-mail, so adding new E-mail.
                log.trace("User is found by EAD but not by E-mail, so adding new E-mail to the user if it presents.");
                loginAuthenticationHelper.saveNewEmailAddressForUserIfEmailIsNotNull(extractedOAuth2UserRequestDataDto.getEmailAddress(), userAccEntity);
                break;
            case OnlyByEmail:
                //User is found by E-mail but not by EAD, so adding new EAD.
                log.trace("User is found by E-mail but not by EAD, so adding new EAD to the user.");
                oAuth2ProviderService.saveExternalAccountDetailForUserAcc(extractedOAuth2UserRequestDataDto.getExternalAccountDetail(), userAccEntity);
                break;
            case ByNeither:
                log.trace("User is not found neither by E-mail nor by EAD.");
                userAccEntity = attemptToRegisterNewUser(extractedOAuth2UserRequestDataDto, oAuth2ProviderService);
                log.info("Registered new user ({}) from: {}", userAccEntity.getId(), providerRegistration);
                break;
            default:
                throw new IllegalStateException("This code part shouldn't have been reached!");
        }

        Objects.requireNonNull(userAccEntity, "userAccEntity shouldn't be null here!");
        return new CustomOauth2User(loginAuthenticationHelper.reloadUserAccFromDB(userAccEntity));
    }

    private UserAccountResolutionSource determineUserAccountResolvingResult(boolean userFoundByExternalAccountDetail, boolean userFoundByEmailAddress) {
        if (userFoundByExternalAccountDetail && userFoundByEmailAddress) {
            return ByBoth;
        }
        if (userFoundByExternalAccountDetail) {
            return OnlyByExternalAccountDetail;
        }
        if (userFoundByEmailAddress) {
            return OnlyByEmail;
        }
        return ByNeither;
    }

    private UserAcc attemptToRegisterNewUser(final ExtractedOAuth2UserRequestDataDto extractedOAuth2UserRequestDataDto, AbstractOAuth2ProviderService oAuth2ProviderService) {
        final JoinFlowSessionDto joinFlowSessionDto = JoinFlowSessionDto.readFromCurrentSession();
        if (joinFlowSessionDto == null || joinFlowSessionDto.getRegistrationRequestId() == null) {
            throw new CannotFindExistingAccountAndNoRegistrationSessionDataIsSetAuthenticationException("Cannot find existing user account or e-mail that this login can be merged to!");
        }

        final RegistrationRequest registrationRequest = getValidRegistrationRequestById(joinFlowSessionDto.getRegistrationRequestId());

        final UserAcc registeredUserAccEntity = initNewUserAccEntity(extractedOAuth2UserRequestDataDto);
        oAuth2ProviderService.saveExternalAccountDetailForUserAcc(extractedOAuth2UserRequestDataDto.getExternalAccountDetail(), registeredUserAccEntity);

        loginAuthenticationHelper.saveNewEmailAddressForUserIfEmailIsNotNull(extractedOAuth2UserRequestDataDto.getEmailAddress(), registeredUserAccEntity);
        //Saving both mails to the user if they are different
        if (!registrationRequest.getEmail().equals(extractedOAuth2UserRequestDataDto.getEmailAddress())) {
            loginAuthenticationHelper.saveNewEmailAddressForUserIfEmailIsNotNull(registrationRequest.getEmail(), registeredUserAccEntity);
        }

        joinFlowSessionDto.setRegistrationRequestId(null);
        joinFlowSessionDto.writeToCurrentSession();
        registrationRequest.setPhase(RegistrationRequestPhase.REGISTERED);
        registrationRequestRepository.save(registrationRequest);

        return registeredUserAccEntity;
    }

    private RegistrationRequest getValidRegistrationRequestById(long registrationRequestId) {
        Optional<RegistrationRequest> registrationRequestOptional = registrationRequestRepository.findById(registrationRequestId);
        if (registrationRequestOptional.isEmpty() || registrationRequestOptional.get().getPhase() != RegistrationRequestPhase.EMAIL_VERIFIED) {
            throw new RegistrationRequestReferredBySessionDataIsInvalidAuthenticationException("Cannot found existing account neither registration request with verified e-mail!");
        }
        return registrationRequestOptional.get();
    }

    private UserAcc initNewUserAccEntity(final ExtractedOAuth2UserRequestDataDto extractedOAuth2UserRequestDataDto) {
        UserAcc newUserAcc = new UserAcc();

        newUserAcc.setEnabled(true);
        newUserAcc.setAuthorities_FromEnumBasedAuthority(Set.of(new EnumBasedAuthority(Authority.User)));

        newUserAcc.setFirstName(extractedOAuth2UserRequestDataDto.getFirstName());
        newUserAcc.setLastName(extractedOAuth2UserRequestDataDto.getLastName());
        newUserAcc.setNickName(extractedOAuth2UserRequestDataDto.getNickName());

        userAccRepository.save(newUserAcc);
        return newUserAcc;
    }
}
