package com.laboschqpa.server.api.service;

import com.laboschqpa.server.entity.EmailVerificationRequest;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.enums.EmailVerificationPhase;
import com.laboschqpa.server.enums.apierrordescriptor.EmailAddressApiError;
import com.laboschqpa.server.exceptions.apierrordescriptor.ContentNotFoundException;
import com.laboschqpa.server.exceptions.apierrordescriptor.EmailAddressException;
import com.laboschqpa.server.repo.EmailVerificationRequestRepository;
import com.laboschqpa.server.repo.UserEmailAddressRepository;
import com.laboschqpa.server.service.mailing.QpaEmailDispatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.security.SecureRandom;

@Log4j2
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

        transactionTemplate.executeWithoutResult(transactionStatus -> {
            emailVerificationRequestRepository.save(emailVerificationRequest);
            emailVerificationRequestRepository.flush();

            qpaEmailDispatcher.sendSyncEmailVerificationRequestMail(emailToRegister,
                    emailVerificationRequest.getId(), emailVerificationRequest.getVerificationKey());
        });
    }

    private String generateVerificationKey() {
        return RandomStringUtils.random(40, 0, 0, true, true, null, new SecureRandom());
    }

    @Transactional
    public void deleteDeleteOwnEmailAddress(long idToDelete, long loggedInUserId) {
        int deletedRowCount;
        if ((deletedRowCount = userEmailAddressRepository.deleteByIdAndAndUserIdAndGetDeletedRowCount(idToDelete, loggedInUserId)) != 1) {
            throw new ContentNotFoundException("Count of deleted rows is " + deletedRowCount + "! The address may not be owned by you.");
        }
        log.info("UserEmailAddress {} deleted by user {}.", idToDelete, loggedInUserId);
    }
}
