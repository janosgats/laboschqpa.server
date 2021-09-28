package com.laboschqpa.server.service.useracc;

import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.account.UserEmailAddress;
import com.laboschqpa.server.entity.account.externalaccountdetail.ExternalAccountDetail;
import com.laboschqpa.server.exceptions.authentication.EmailBelongsToAnOtherAccountAuthenticationException;
import com.laboschqpa.server.repo.UserEmailAddressRepository;
import com.laboschqpa.server.service.loginauthentication.LoginAuthenticationHelper;
import com.laboschqpa.server.service.loginauthentication.UserAccResolutionSource;
import com.laboschqpa.server.service.oauth2.OAuth2Provider;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import static com.laboschqpa.server.service.loginauthentication.UserAccResolutionSource.*;

@Log4j2
@RequiredArgsConstructor
@Service
public class UserAccResolverService {
    private final UserEmailAddressRepository userEmailAddressRepository;
    private final LoginAuthenticationHelper loginAuthenticationHelper;

    public UserAccResolutionResult resolve(OAuth2Provider oAuth2Provider,
                                           String emailAddress,
                                           ExternalAccountDetail externalAccountDetail) {
        final UserEmailAddress userEmailAddressTriedToLoadFromDB;
        if (StringUtils.isNotBlank(emailAddress)) {
            userEmailAddressTriedToLoadFromDB = userEmailAddressRepository
                    .findByEmail(emailAddress).orElse(null);
        } else {
            userEmailAddressTriedToLoadFromDB = null;
        }

        final boolean userFoundByEmailAddress = userEmailAddressTriedToLoadFromDB != null;

        final UserAcc userAccEntityByEmail;
        if (userFoundByEmailAddress) {
            userAccEntityByEmail = userEmailAddressTriedToLoadFromDB.getUserAcc();
        } else {
            userAccEntityByEmail = null;
        }

        final UserAcc userAccEntityByEAD = oAuth2Provider.loadUserAccFromDbByExternalAccountDetail(externalAccountDetail);
        final boolean userFoundByExternalAccountDetail = userAccEntityByEAD != null;

        final UserAccResolutionSource accountResolutionSource = determineResolutionResult(userFoundByExternalAccountDetail, userFoundByEmailAddress);

        final UserAcc resolvedUserAccEntity;
        switch (accountResolutionSource) {
            case ByBoth:
                log.trace("User is found both by EAD and by E-mail.");
                if (!userAccEntityByEAD.getId().equals(userAccEntityByEmail.getId())) {
                    log.info("During login user into session: User is found both by EAD and by E-mail, but they are NOT the same user.");
                    //If the user is found by EAD and also by Email, than the two users HAVE TO BE THE SAME!
                    throw new EmailBelongsToAnOtherAccountAuthenticationException(
                            "E-mail is saved in the system as a different User's e-mail address: "
                                    + userEmailAddressTriedToLoadFromDB.getEmail()
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

        log.trace("User Account Resolved. Digest: userId: {}, accountResolutionSource: {}",
                resolvedUserAccEntity != null ? resolvedUserAccEntity.getId() : "null", accountResolutionSource);

        return new UserAccResolutionResult(accountResolutionSource, resolvedUserAccEntity);
    }

    private UserAccResolutionSource determineResolutionResult(boolean userFoundByExternalAccountDetail, boolean userFoundByEmailAddress) {
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
}
