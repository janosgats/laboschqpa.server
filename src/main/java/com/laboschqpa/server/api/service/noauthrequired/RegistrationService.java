package com.laboschqpa.server.api.service.noauthrequired;

import com.laboschqpa.server.api.dto.join.CreateNewAccountRequest;
import com.laboschqpa.server.config.helper.EnumBasedAuthority;
import com.laboschqpa.server.config.userservice.CustomOauth2User;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.account.UserJoinCircumstance;
import com.laboschqpa.server.entity.account.externalaccountdetail.ExternalAccountDetail;
import com.laboschqpa.server.enums.apierrordescriptor.RegistrationApiError;
import com.laboschqpa.server.enums.auth.Authority;
import com.laboschqpa.server.enums.auth.OAuth2ProviderRegistration;
import com.laboschqpa.server.exceptions.apierrordescriptor.RegistrationException;
import com.laboschqpa.server.model.sessiondto.RegistrationSessionDto;
import com.laboschqpa.server.repo.AcceptedEmailRepository;
import com.laboschqpa.server.repo.UserAccRepository;
import com.laboschqpa.server.repo.UserJoinCircumstanceRepository;
import com.laboschqpa.server.service.loginauthentication.LoginAuthenticationHelper;
import com.laboschqpa.server.service.loginauthentication.UserAccResolutionSource;
import com.laboschqpa.server.service.oauth2.OAuth2Provider;
import com.laboschqpa.server.service.oauth2.OAuth2ProviderFactory;
import com.laboschqpa.server.service.useracc.UserAccResolutionResult;
import com.laboschqpa.server.service.useracc.UserAccResolverService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.Set;

@Log4j2
@RequiredArgsConstructor
@Service
public class RegistrationService {
    private final TransactionTemplate transactionTemplate;
    private final OAuth2ProviderFactory oAuth2ProviderFactory;
    private final LoginAuthenticationHelper loginAuthenticationHelper;
    private final UserAccRepository userAccRepository;
    private final UserAccResolverService userAccResolverService;
    private final AcceptedEmailRepository acceptedEmailRepository;
    private final UserJoinCircumstanceRepository userJoinCircumstanceRepository;

    @AllArgsConstructor
    private static class ExplodedRegistration {
        final RegistrationSessionDto registrationSessionDto;
        final OAuth2ProviderRegistration providerRegistration;
        final OAuth2Provider oAuth2Provider;
        final ExternalAccountDetail externalAccountDetail;
    }

    public UserAcc createNewAccountFromSessionOAuthInfo(CreateNewAccountRequest request) {
        ExplodedRegistration exploded = getExplodedRegistrationSessionDto();

        assertUserAccDoesNotExistYet(exploded.registrationSessionDto, exploded.oAuth2Provider,
                exploded.registrationSessionDto.getEmailAddress(), exploded.externalAccountDetail);

        UserAcc registeredUserAcc = registerNewUser(exploded, request);

        logInNewlyCreatedUser(registeredUserAcc, exploded.providerRegistration);
        return registeredUserAcc;
    }

    private void logInNewlyCreatedUser(UserAcc userAcc, OAuth2ProviderRegistration providerRegistration) {
        CustomOauth2User customOauth2User = new CustomOauth2User(userAcc);
        Authentication authentication = new OAuth2AuthenticationToken(customOauth2User,
                customOauth2User.getAuthorities(), providerRegistration.getProviderRegistrationKey());

        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(authentication);
    }

    private ExplodedRegistration getExplodedRegistrationSessionDto() {
        final RegistrationSessionDto registrationSessionDto = RegistrationSessionDto.readFromCurrentSession();
        if (registrationSessionDto == null) {
            throw new RegistrationException(RegistrationApiError.NO_REGISTRATION_INFO_FOUND_IN_SESSION);
        }

        OAuth2ProviderRegistration providerRegistration
                = OAuth2ProviderRegistration.fromRegistrationKey(registrationSessionDto.getOauth2ProviderRegistrationKey());
        if (providerRegistration == null) {
            log.error("Invalid providerRegistrationKey in RegistrationSessionDto!");
            throw new RuntimeException("Invalid providerRegistrationKey in RegistrationSessionDto");
        }
        OAuth2Provider oAuth2Provider = oAuth2ProviderFactory.get(providerRegistration);

        if (StringUtils.isBlank(registrationSessionDto.getOauth2ExternalAccountDetailString())) {
            log.error("Invalid externalAccountDetailString in RegistrationSessionDto!");
            throw new RuntimeException("Invalid externalAccountDetailString in RegistrationSessionDto");
        }
        ExternalAccountDetail externalAccountDetail = oAuth2Provider.instantiateExternalAccountDetail();
        externalAccountDetail.fillFromDetailString(registrationSessionDto.getOauth2ExternalAccountDetailString());

        return new ExplodedRegistration(registrationSessionDto, providerRegistration, oAuth2Provider, externalAccountDetail);
    }

    private void assertUserAccDoesNotExistYet(RegistrationSessionDto registrationSessionDto,
                                              OAuth2Provider oAuth2Provider,
                                              String emailAddress,
                                              ExternalAccountDetail externalAccountDetail) {
        UserAccResolutionResult accountResolutionResult
                = userAccResolverService.resolve(oAuth2Provider, emailAddress, externalAccountDetail);

        if (accountResolutionResult.getResolutionSource() != UserAccResolutionSource.ByNeither) {
            registrationSessionDto.removeFromCurrentSession();
            throw new RegistrationException(RegistrationApiError.USER_ACCOUNT_ALREADY_EXISTS);
        }
    }

    private UserAcc registerNewUser(ExplodedRegistration exploded, CreateNewAccountRequest request) {
        return transactionTemplate.execute((transactionStatus) -> {
            final UserAcc registeredUserAccEntity = initNewUserAccEntity(exploded.registrationSessionDto);
            exploded.oAuth2Provider
                    .saveExternalAccountDetailForUserAcc(exploded.externalAccountDetail, registeredUserAccEntity);

            loginAuthenticationHelper.saveNewEmailAddressForUserIfNotBlank(
                    exploded.registrationSessionDto.getEmailAddress(), registeredUserAccEntity, true);

            acceptedEmailRepository.recalculateByUserId(registeredUserAccEntity.getId());

            final UserJoinCircumstance userJoinCircumstance = new UserJoinCircumstance();
            userJoinCircumstance.setUserAcc(registeredUserAccEntity);
            userJoinCircumstance.setJoinUrl(request.getJoinUrl());
            userJoinCircumstance.setOauth2Provider(exploded.providerRegistration.getProviderRegistrationKey());
            userJoinCircumstance.setEmail(exploded.registrationSessionDto.getEmailAddress());
            userJoinCircumstance.setName(exploded.registrationSessionDto.getNameDigest());
            userJoinCircumstance.setCreated(Instant.now());
            userJoinCircumstanceRepository.save(userJoinCircumstance);

            exploded.registrationSessionDto.removeFromCurrentSession();
            return registeredUserAccEntity;
        });
    }

    private UserAcc initNewUserAccEntity(RegistrationSessionDto registrationSessionDto) {
        UserAcc newUserAcc = new UserAcc();

        newUserAcc.setEnabled(true);
        newUserAcc.setAuthorities_FromEnumBasedAuthority(Set.of(new EnumBasedAuthority(Authority.User)));
        newUserAcc.setIsAcceptedByEmail(false);

        newUserAcc.setFirstName(registrationSessionDto.getFirstName());
        newUserAcc.setLastName(registrationSessionDto.getLastName());
        newUserAcc.setNickName(registrationSessionDto.getNickName());
        if (StringUtils.isNotBlank(registrationSessionDto.getProfilePicUrl())) {
            newUserAcc.setProfilePicUrl(registrationSessionDto.getProfilePicUrl());
        }

        newUserAcc.setRegistered(Instant.now());

        userAccRepository.save(newUserAcc);
        return newUserAcc;
    }
}
