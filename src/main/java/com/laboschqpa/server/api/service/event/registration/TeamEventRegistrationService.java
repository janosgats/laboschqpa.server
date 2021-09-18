package com.laboschqpa.server.api.service.event.registration;

import com.laboschqpa.server.api.service.TeamService;
import com.laboschqpa.server.entity.Team;
import com.laboschqpa.server.entity.event.Event;
import com.laboschqpa.server.entity.event.TeamEventRegistration;
import com.laboschqpa.server.enums.apierrordescriptor.EventApiError;
import com.laboschqpa.server.enums.event.EventTarget;
import com.laboschqpa.server.exceptions.apierrordescriptor.ContentNotFoundException;
import com.laboschqpa.server.exceptions.apierrordescriptor.EventException;
import com.laboschqpa.server.repo.event.TeamEventRegistrationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Log4j2
@RequiredArgsConstructor
@Service
public class TeamEventRegistrationService {
    private final EventRegistrationHelper helper;
    private final TeamEventRegistrationRepository teamEventRegistrationRepository;
    private final TeamService teamService;

    public void register(long teamId, long eventId) {
        final Team team = teamService.getById(teamId);
        final Event event = getExistingOpenTeamEvent(eventId);
        if (teamEventRegistrationRepository.findByTeamAndEvent(team, event).isPresent()) {
            throw new EventException(EventApiError.ALREADY_REGISTERED);
        }

        helper.assertRegistrationLimit(event, false);

        createTeamRegistration(team, event);

        if (!helper.isRegistrationLimitAlright(event, true)) {
            deleteTeamRegistration(team, event);
            throw new EventException(EventApiError.REGISTRATION_LIMIT_EXCEEDED);
        }
    }

    public void deRegister(long teamId, long eventId) {
        final Team team = teamService.getById(teamId);
        final Event event = getExistingOpenTeamEvent(eventId);

        deleteTeamRegistration(team, event);
    }

    public void deleteAllRegistrationsOfTeam(Team team) {
        teamEventRegistrationRepository.deleteAllRegistrationsOfTeam(team);
    }

    private void createTeamRegistration(Team team, Event event) {
        TeamEventRegistration registration = new TeamEventRegistration();
        registration.setEvent(event);
        registration.setTeam(team);
        registration.setCreated(Instant.now());
        teamEventRegistrationRepository.save(registration);
    }

    private void deleteTeamRegistration(Team team, Event event) {
        int deletedRowCount;
        if ((deletedRowCount = teamEventRegistrationRepository.deleteByTeamAndEvent_andGetDeletedRowCount(team, event)) != 1) {
            throw new ContentNotFoundException("Count of deleted rows is " + deletedRowCount + "!");
        }
    }

    public Event getExistingOpenTeamEvent(long eventId) {
        final Event event = helper.getExistingOpenEvent(eventId);
        if (event.getTarget() != EventTarget.TEAM) {
            throw new EventException(EventApiError.EVENT_IS_NOT_TEAM_EVENT);
        }
        return event;
    }
}
