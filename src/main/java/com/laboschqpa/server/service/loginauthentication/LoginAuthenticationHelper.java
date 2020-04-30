package com.laboschqpa.server.service.loginauthentication;

import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.account.UserEmailAddress;
import com.laboschqpa.server.exceptions.authentication.CorruptedContextAuthenticationException;
import com.laboschqpa.server.exceptions.authentication.UserAccountIsDisabledAuthenticationException;
import com.laboschqpa.server.repo.UserAccRepository;
import com.laboschqpa.server.repo.UserEmailAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class LoginAuthenticationHelper {
    private final UserAccRepository userAccRepository;
    private final UserEmailAddressRepository userEmailAddressRepository;

    public void assertUserAccIsEnabled(UserAcc userAccEntity) {
        if (!userAccEntity.getEnabled()) {
            throw new UserAccountIsDisabledAuthenticationException("The user account is disabled!.");
        }
    }

    public void saveNewEmailAddressForUserIfEmailIsNotNull(String emailAddress, UserAcc userAccEntity) {
        if (emailAddress != null) {
            UserEmailAddress newUserEmailAddress = new UserEmailAddress();
            newUserEmailAddress.setEmail(emailAddress);
            newUserEmailAddress.setUserAcc(userAccEntity);
            userEmailAddressRepository.save(newUserEmailAddress);
        }
    }

    public UserAcc reloadUserAccFromDB(UserAcc previousUserAcc) {
        Optional<UserAcc> userAccOptional = userAccRepository.findById(previousUserAcc.getId());
        if (userAccOptional.isEmpty()) {
            throw new CorruptedContextAuthenticationException("Cannot reload user account. Please contact support about this error message!");
        }

        UserAcc reloadedUserAcc = userAccOptional.get();
        assertUserAccIsEnabled(reloadedUserAcc);
        return reloadedUserAcc;
    }
}
