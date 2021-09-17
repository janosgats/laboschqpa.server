package com.laboschqpa.server.api.service.event;

import com.laboschqpa.server.repo.event.EventRepository;
import com.laboschqpa.server.repo.event.dto.PersonalEventForUserJpaDto;
import com.laboschqpa.server.repo.event.dto.TeamEventForUserJpaDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Service
public class EventService {
    private final EventRepository eventRepository;

    public List<PersonalEventForUserJpaDto> listPersonalEventsFor(long userId) {
        return eventRepository.findAllPersonalEventsForUser(userId);
    }

    public List<TeamEventForUserJpaDto> listTeamEventsFor(long teamId) {
        return eventRepository.findAllTeamEventsForUser(teamId);
    }
}
