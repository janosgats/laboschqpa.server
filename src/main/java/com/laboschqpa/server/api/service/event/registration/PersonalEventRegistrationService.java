package com.laboschqpa.server.api.service.event.registration;

import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.event.Event;
import com.laboschqpa.server.entity.event.PersonalEventRegistration;
import com.laboschqpa.server.enums.apierrordescriptor.EventApiError;
import com.laboschqpa.server.enums.event.EventTarget;
import com.laboschqpa.server.exceptions.apierrordescriptor.ContentNotFoundException;
import com.laboschqpa.server.exceptions.apierrordescriptor.EventException;
import com.laboschqpa.server.repo.event.PersonalEventRegistrationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Log4j2
@RequiredArgsConstructor
@Service
public class PersonalEventRegistrationService {
    private final EventRegistrationHelper helper;
    private final PersonalEventRegistrationRepository personalEventRegistrationRepository;

    public void register(UserAcc userAcc, long eventId) {
        final Event event = getExistingOpenPersonalEvent(eventId);
        if (personalEventRegistrationRepository.findByUserAccAndEvent(userAcc, event).isPresent()) {
            throw new EventException(EventApiError.ALREADY_REGISTERED);
        }

        helper.assertRegistrationLimit(event, false);

        createPersonalRegistration(userAcc, event);

        if (!helper.isRegistrationLimitAlright(event, true)) {
            deletePersonalRegistration(userAcc, event);
            throw new EventException(EventApiError.REGISTRATION_LIMIT_EXCEEDED);
        }
    }

    public void deRegister(UserAcc userAcc, long eventId) {
        final Event event = getExistingOpenPersonalEvent(eventId);
        deletePersonalRegistration(userAcc, event);
    }

    private void createPersonalRegistration(UserAcc userAcc, Event event) {
        PersonalEventRegistration registration = new PersonalEventRegistration();
        registration.setEvent(event);
        registration.setUserAcc(userAcc);
        registration.setTime(Instant.now());
        personalEventRegistrationRepository.save(registration);
    }

    private void deletePersonalRegistration(UserAcc userAcc, Event event) {
        int deletedRowCount;
        if ((deletedRowCount = personalEventRegistrationRepository.deleteByUserAccAndEvent_andGetDeletedRowCount(userAcc, event)) != 1) {
            throw new ContentNotFoundException("Count of deleted rows is " + deletedRowCount + "!");
        }
    }

    private Event getExistingOpenPersonalEvent(long eventId) {
        final Event event = helper.getExistingOpenEvent(eventId);
        if (event.getTarget() != EventTarget.PERSONAL) {
            throw new EventException(EventApiError.EVENT_IS_NOT_PERSONAL_EVENT);
        }
        return event;
    }
}
