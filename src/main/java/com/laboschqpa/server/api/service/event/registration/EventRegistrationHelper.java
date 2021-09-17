package com.laboschqpa.server.api.service.event.registration;

import com.laboschqpa.server.entity.event.Event;
import com.laboschqpa.server.enums.apierrordescriptor.EventApiError;
import com.laboschqpa.server.exceptions.apierrordescriptor.ContentNotFoundException;
import com.laboschqpa.server.exceptions.apierrordescriptor.EventException;
import com.laboschqpa.server.repo.event.EventRepository;
import com.laboschqpa.server.repo.event.PersonalEventRegistrationRepository;
import com.laboschqpa.server.repo.event.TeamEventRegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@RequiredArgsConstructor
@Service
public class EventRegistrationHelper {
    private final EventRepository eventRepository;
    private final PersonalEventRegistrationRepository personalEventRegistrationRepository;
    private final TeamEventRegistrationRepository teamEventRegistrationRepository;

    public void assertRegistrationLimit(Event event, boolean isEqualityOkay) {
        if (!isRegistrationLimitAlright(event, isEqualityOkay)) {
            throw new EventException(EventApiError.REGISTRATION_LIMIT_EXCEEDED);
        }
    }

    public boolean isRegistrationLimitAlright(Event event, boolean isEqualityOkay) {
        if (event.getRegistrationLimit() == null) {
            return true;
        }

        final int countOfRegisteredOnes = getCountOfRegisteredOnes(event);
        if (isEqualityOkay) {
            return countOfRegisteredOnes <= event.getRegistrationLimit();
        }
        return countOfRegisteredOnes < event.getRegistrationLimit();
    }

    private int getCountOfRegisteredOnes(Event event) {
        switch (event.getTarget()) {
            case PERSONAL:
                return personalEventRegistrationRepository.countByEvent(event);
            case TEAM:
                return teamEventRegistrationRepository.countByEvent(event);
            default:
                throw new IllegalStateException("Unexpected value: " + event.getTarget());
        }
    }

    public Event getExistingOpenEvent(long eventId) {
        final Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ContentNotFoundException("Event does not exist with id: " + eventId));
        if (event.getRegistrationDeadline() != null && event.getRegistrationDeadline().isBefore(Instant.now())) {
            throw new EventException(EventApiError.DEADLINE_HAS_PASSED);
        }
        return event;
    }
}
