package com.laboschqpa.server.api.service;

import com.laboschqpa.server.api.dto.teamscore.CreateNewTeamScoreDto;
import com.laboschqpa.server.api.dto.teamscore.EditTeamScoreDto;
import com.laboschqpa.server.entity.Team;
import com.laboschqpa.server.entity.TeamScore;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.usergeneratedcontent.Objective;
import com.laboschqpa.server.enums.apierrordescriptor.TeamScoreApiError;
import com.laboschqpa.server.exceptions.apierrordescriptor.ContentNotFoundException;
import com.laboschqpa.server.exceptions.apierrordescriptor.TeamScoreException;
import com.laboschqpa.server.repo.TeamRepository;
import com.laboschqpa.server.repo.TeamScoreRepository;
import com.laboschqpa.server.repo.usergeneratedcontent.ObjectiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
@Service
public class TeamScoreService {
    private final TeamScoreRepository teamScoreRepository;
    private final ObjectiveRepository objectiveRepository;
    private final TeamRepository teamRepository;

    public TeamScore getTeamScore(Long objectiveId) {
        Optional<TeamScore> teamScoreOptional = teamScoreRepository.findById(objectiveId);

        if (teamScoreOptional.isEmpty())
            throw new ContentNotFoundException("Cannot find TeamScore with Id: " + objectiveId);

        return teamScoreOptional.get();
    }

    public Optional<TeamScore> find(Long objectiveId, Long teamId) {
        return teamScoreRepository.findByObjectiveIdAndTeamId(objectiveId, teamId);
    }

    public TeamScore createNewTeamScore(CreateNewTeamScoreDto createNewTeamScoreDto, UserAcc creatorUserAcc) {
        Pair<Objective, Team> objectiveTeamPair = getExistingObjectiveAndTeam(createNewTeamScoreDto.getObjectiveId(), createNewTeamScoreDto.getTeamId());

        TeamScore teamScore = new TeamScore();
        teamScore.setScore(createNewTeamScoreDto.getScore());
        teamScore.setObjective(objectiveTeamPair.getLeft());
        teamScore.setTeam(objectiveTeamPair.getRight());

        teamScoreRepository.save(teamScore);
        log.info("TeamScore {} created by user {}.", teamScore.getId(), creatorUserAcc.getId());
        return teamScore;
    }

    public void editTeamScore(EditTeamScoreDto editTeamScoreDto, UserAcc editorUserAcc) {
        Optional<TeamScore> teamScoreOptional = teamScoreRepository.findById(editTeamScoreDto.getId());
        if (teamScoreOptional.isEmpty())
            throw new ContentNotFoundException("Cannot find TeamScore with Id: " + editTeamScoreDto.getId());

        TeamScore teamScore = teamScoreOptional.get();
        teamScore.setScore(editTeamScoreDto.getScore());

        teamScoreRepository.save(teamScore);

        log.info("TeamScore {} edited by user {}.", teamScore.getId(), editorUserAcc.getId());
    }

    @Transactional
    public void deleteTeamScore(Long teamScoreId, UserAcc deleterUserAcc) {
        int deletedRowCount;
        if ((deletedRowCount = teamScoreRepository.deleteByIdAndGetDeletedRowCount(teamScoreId)) != 1) {
            throw new ContentNotFoundException("Count of deleted rows is " + deletedRowCount + "!");
        }

        log.info("TeamScore {} deleted by user {}.", teamScoreId, deleterUserAcc.getId());
    }

    public List<TeamScore> listAllTeamScores() {
        return teamScoreRepository.findAll();
    }

    private Pair<Objective, Team> getExistingObjectiveAndTeam(Long objectiveId, Long teamId) {
        final Optional<Objective> objectiveOptional = objectiveRepository.findById(objectiveId);
        if (objectiveOptional.isEmpty()) {
            throw new TeamScoreException(TeamScoreApiError.OBJECTIVE_IS_NOT_FOUND);
        }
        final Optional<Team> teamOptional = teamRepository.findById(teamId);
        if (teamOptional.isEmpty()) {
            throw new TeamScoreException(TeamScoreApiError.TEAM_IS_NOT_FOUND);
        }

        return Pair.of(objectiveOptional.get(), teamOptional.get());
    }
}
