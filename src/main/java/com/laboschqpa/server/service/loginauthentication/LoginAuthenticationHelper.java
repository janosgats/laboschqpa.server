package com.laboschqpa.server.service.loginauthentication;

import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.account.UserEmailAddress;
import com.laboschqpa.server.exceptions.authentication.CorruptedContextAuthenticationException;
import com.laboschqpa.server.exceptions.authentication.UserAccountIsDisabledAuthenticationException;
import com.laboschqpa.server.repo.UserAccRepository;
import com.laboschqpa.server.repo.UserEmailAddressRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class LoginAuthenticationHelper {
    private final UserAccRepository userAccRepository;
    private final UserEmailAddressRepository userEmailAddressRepository;

    public void assertUserAccIsEnabled(UserAcc userAccEntity) {
        if (!userAccEntity.getEnabled()) {
            throw new UserAccountIsDisabledAuthenticationException("User account " + userAccEntity.getId() + " is disabled.");
        }
    }

    public void saveNewEmailAddressForUserIfNotBlank(String emailAddress, UserAcc userAccEntity) {
        saveNewEmailAddressForUserIfNotBlank(emailAddress, userAccEntity, false);
    }

    public void saveNewEmailAddressForUserIfNotBlank(String emailAddress, UserAcc userAccEntity, boolean doFlush) {
        if (StringUtils.isNotBlank(emailAddress)) {
            UserEmailAddress newUserEmailAddress = new UserEmailAddress();
            newUserEmailAddress.setEmail(emailAddress.trim());
            newUserEmailAddress.setUserAcc(userAccEntity);
            userEmailAddressRepository.save(newUserEmailAddress);
            if (doFlush) {
                userEmailAddressRepository.flush();
            }
        }
    }

    public UserAcc reloadUserAccFromDB(UserAcc previousUserAcc) {
        Optional<UserAcc> userAccOptional = userAccRepository.findByIdWithAuthorities(previousUserAcc.getId());
        if (userAccOptional.isEmpty()) {
            throw new CorruptedContextAuthenticationException("Cannot reload user account. Please contact support about this error message!");
        }

        UserAcc reloadedUserAcc = userAccOptional.get();
        assertUserAccIsEnabled(reloadedUserAcc);
        return reloadedUserAcc;
    }
}
