package com.laboschqpa.server.api.service;

import com.laboschqpa.server.entity.EmailVerificationRequest;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.enums.EmailVerificationPhase;
import com.laboschqpa.server.enums.apierrordescriptor.EmailAddressApiError;
import com.laboschqpa.server.exceptions.apierrordescriptor.EmailAddressException;
import com.laboschqpa.server.repo.EmailVerificationRequestRepository;
import com.laboschqpa.server.repo.UserEmailAddressRepository;
import com.laboschqpa.server.service.mailing.QpaEmailDispatcher;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.security.SecureRandom;

@RequiredArgsConstructor
@Service
public class EmailAddressService {
    private final TransactionTemplate transactionTemplate;
    private final EmailVerificationRequestRepository emailVerificationRequestRepository;
    private final UserEmailAddressRepository userEmailAddressRepository;
    private final QpaEmailDispatcher qpaEmailDispatcher;

    public void onSubmitNewEmail(UserAcc userAcc, String emailToRegister) {
        if (userEmailAddressRepository.findByEmail(emailToRegister).isPresent()) {
            throw new EmailAddressException(EmailAddressApiError.EMAIL_ALREADY_BELONGS_TO_A_USER,
                    "The e-mail address already belongs to someone!", emailToRegister);
        }

        EmailVerificationRequest emailVerificationRequest = new EmailVerificationRequest();
        emailVerificationRequest.setEmail(emailToRegister);
        emailVerificationRequest.setUserAcc(userAcc);
        emailVerificationRequest.setVerificationKey(generateVerificationKey());
        emailVerificationRequest.setPhase(EmailVerificationPhase.EMAIL_SUBMITTED);

        qpaEmailDispatcher.sendSyncEmailVerificationRequestMail(emailToRegister,
                emailVerificationRequest.getId(), emailVerificationRequest.getVerificationKey());

        emailVerificationRequestRepository.save(emailVerificationRequest);
    }

    private String generateVerificationKey() {
        return RandomStringUtils.random(40, 0, 0, true, true, null, new SecureRandom());
    }
}
