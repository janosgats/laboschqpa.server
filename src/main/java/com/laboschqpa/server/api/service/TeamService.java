package com.laboschqpa.server.api.service;

import com.laboschqpa.server.entity.Team;
import com.laboschqpa.server.exceptions.apierrordescriptor.ContentNotFoundException;
import com.laboschqpa.server.repo.TeamRepository;
import com.laboschqpa.server.repo.dto.TeamMemberJpaDto;
import com.laboschqpa.server.repo.dto.TeamWithScoreJpaDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Service
public class TeamService {
    private final TeamRepository teamRepository;

    public List<TeamWithScoreJpaDto> listActiveTeamsWithScores() {
        return teamRepository.findAllWithScoreByArchivedIsFalseOrderByScoreDesc();
    }

    public Team getById(long id) {
        return getValidTeam(id);
    }

    public List<TeamMemberJpaDto> listMembers(long id) {
        getValidTeam(id);
        return teamRepository.findAllMembers(id);
    }

    private Team getValidTeam(long id) {
        return teamRepository.findById(id).orElseThrow(() -> new ContentNotFoundException("Team with id " + id + " is not found."));
    }
}
