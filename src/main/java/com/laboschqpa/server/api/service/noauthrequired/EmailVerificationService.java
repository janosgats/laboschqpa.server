package com.laboschqpa.server.api.service.noauthrequired;

import com.laboschqpa.server.entity.EmailVerificationRequest;
import com.laboschqpa.server.entity.account.UserEmailAddress;
import com.laboschqpa.server.enums.EmailVerificationPhase;
import com.laboschqpa.server.enums.apierrordescriptor.EmailAddressApiError;
import com.laboschqpa.server.exceptions.apierrordescriptor.EmailAddressException;
import com.laboschqpa.server.repo.AcceptedEmailRepository;
import com.laboschqpa.server.repo.EmailVerificationRequestRepository;
import com.laboschqpa.server.repo.UserEmailAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@RequiredArgsConstructor
@Service
public class EmailVerificationService {
    private final TransactionTemplate transactionTemplate;
    private final EmailVerificationRequestRepository emailVerificationRequestRepository;
    private final UserEmailAddressRepository userEmailAddressRepository;
    private final AcceptedEmailRepository acceptedEmailRepository;

    public void onVisitingPageFromVerificationEmailLink(long requestId, String verificationKey) {
        EmailVerificationRequest validRequest = getValidatedVerificationRequest(requestId, verificationKey);

        UserEmailAddress userEmailAddress = new UserEmailAddress();
        userEmailAddress.setUserAcc(validRequest.getUserAcc());
        userEmailAddress.setEmail(validRequest.getEmail().trim());

        transactionTemplate.executeWithoutResult(transactionStatus -> {
            try {
                userEmailAddressRepository.save(userEmailAddress);
                userEmailAddressRepository.flush();
            } catch (DataIntegrityViolationException e) {
                if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
                    throw new EmailAddressException(EmailAddressApiError.EMAIL_ALREADY_BELONGS_TO_A_USER, e);
                }
                throw e;
            }

            acceptedEmailRepository.recalculateByUserId(validRequest.getUserAcc().getId());
        });

        validRequest.setPhase(EmailVerificationPhase.EMAIL_VERIFIED);
        emailVerificationRequestRepository.save(validRequest);
    }

    private EmailVerificationRequest getValidatedVerificationRequest(long id, String submittedVerificationKey) {
        EmailVerificationRequest expectedRequest = emailVerificationRequestRepository.findById(id)
                .orElseThrow(() -> new EmailAddressException(EmailAddressApiError.VERIFICATION_REQUEST_CANNOT_BE_FOUND,
                        "Verification request cannot be found!", id));

        if (!expectedRequest.getPhase().equals(EmailVerificationPhase.EMAIL_SUBMITTED)) {
            throw new EmailAddressException(EmailAddressApiError.VERIFICATION_REQUEST_PHASE_IS_INVALID);
        }

        if (expectedRequest.getVerificationKey().equals(submittedVerificationKey)) {
            return expectedRequest;
        }
        throw new EmailAddressException(EmailAddressApiError.VERIFICATION_REQUEST_KEY_IS_NOT_MATCHING);
    }
}
