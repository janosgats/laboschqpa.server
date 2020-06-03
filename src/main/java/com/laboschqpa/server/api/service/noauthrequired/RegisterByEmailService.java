package com.laboschqpa.server.api.service.noauthrequired;

import com.laboschqpa.server.entity.RegistrationRequest;
import com.laboschqpa.server.enums.RegistrationRequestPhase;
import com.laboschqpa.server.enums.apierrordescriptor.RegistrationApiError;
import com.laboschqpa.server.exceptions.apierrordescriptor.RegistrationException;
import com.laboschqpa.server.model.sessiondto.JoinFlowSessionDto;
import com.laboschqpa.server.repo.RegistrationRequestRepository;
import com.laboschqpa.server.repo.UserEmailAddressRepository;
import com.laboschqpa.server.service.mailing.QpaEmailDispatcher;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RegisterByEmailService {
    private final RegistrationRequestRepository registrationRequestRepository;
    private final UserEmailAddressRepository userEmailAddressRepository;
    private final QpaEmailDispatcher qpaEmailDispatcher;

    public void onSubmitEmailToRegister(String emailToRegister) {
        if (userEmailAddressRepository.findByEmail(emailToRegister).isPresent()) {
            throw new RegistrationException(RegistrationApiError.E_MAIL_ADDRESS_IS_ALREADY_IN_THE_SYSTEM,
                    "The e-mail address is already present in the system!", emailToRegister);
        }

        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setEmail(emailToRegister);
        registrationRequest.setKey(RandomStringUtils.random(100, 0, 0, true, true, null, new SecureRandom()));
        registrationRequest.setPhase(RegistrationRequestPhase.EMAIL_SUBMITTED);
        registrationRequestRepository.save(registrationRequest);

        qpaEmailDispatcher.sendSyncRegistrationRequestMail(emailToRegister, registrationRequest.getId(), registrationRequest.getKey());
    }

    public void onVisitingPageFromEmailLink(Long registrationRequestId, String registrationRequestKey) {
        RegistrationRequest registrationRequest = getValidRegistrationRequest(registrationRequestId, registrationRequestKey);

        JoinFlowSessionDto joinFlowSessionDto = new JoinFlowSessionDto();
        joinFlowSessionDto.setRegistrationRequestId(registrationRequest.getId());
        joinFlowSessionDto.writeToCurrentSession();

        registrationRequest.setPhase(RegistrationRequestPhase.EMAIL_VERIFIED);
        registrationRequestRepository.save(registrationRequest);
    }

    private RegistrationRequest getValidRegistrationRequest(Long registrationRequestId, String submittedRegistrationRequestKey) {
        Optional<RegistrationRequest> registrationRequestOptional = registrationRequestRepository.findById(registrationRequestId);
        if (registrationRequestOptional.isEmpty()) {
            throw new RegistrationException(RegistrationApiError.REGISTRATION_REQUEST_CANNOT_BE_FOUND,
                    "Registration request cannot be found!", registrationRequestId);
        }
        RegistrationRequest expectedRegistrationRequest = registrationRequestOptional.get();

        if (!expectedRegistrationRequest.getPhase().equals(RegistrationRequestPhase.EMAIL_SUBMITTED) && !expectedRegistrationRequest.getPhase().equals(RegistrationRequestPhase.EMAIL_VERIFIED)) {
            throw new RegistrationException(RegistrationApiError.REGISTRATION_REQUEST_IS_IN_AN_INVALID_PHASE,
                    "Registration request is not in the appropriate phase to verify the e-mail address. " +
                            "Please submit a new registration request if you don't have an account yet!",
                    expectedRegistrationRequest.getPhase());
        }

        if (expectedRegistrationRequest.getKey().equals(submittedRegistrationRequestKey)) {
            return expectedRegistrationRequest;
        } else {
            throw new RegistrationException(RegistrationApiError.REGISTRATION_REQUEST_KEY_IS_NOT_MATCHING,
                    "Registration request key is not matching!", submittedRegistrationRequestKey);
        }
    }
}
