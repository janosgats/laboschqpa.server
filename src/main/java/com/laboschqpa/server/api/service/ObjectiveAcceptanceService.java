package com.laboschqpa.server.api.service;

import com.laboschqpa.server.api.dto.objectiveacceptance.SetObjectiveAcceptanceRequest;
import com.laboschqpa.server.entity.ObjectiveAcceptance;
import com.laboschqpa.server.entity.Team;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.usergeneratedcontent.Objective;
import com.laboschqpa.server.enums.apierrordescriptor.ObjectiveAcceptanceApiError;
import com.laboschqpa.server.exceptions.apierrordescriptor.ContentNotFoundException;
import com.laboschqpa.server.exceptions.apierrordescriptor.ObjectiveAcceptanceException;
import com.laboschqpa.server.repo.ObjectiveAcceptanceRepository;
import com.laboschqpa.server.repo.TeamRepository;
import com.laboschqpa.server.repo.usergeneratedcontent.ObjectiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
@Service
public class ObjectiveAcceptanceService {
    private final ObjectiveAcceptanceRepository objectiveAcceptanceRepository;
    private final ObjectiveRepository objectiveRepository;
    private final TeamRepository teamRepository;

    public boolean isAccepted(Long objectiveId, Long teamId) {
        return objectiveAcceptanceRepository.findByObjectiveIdAndTeamId(objectiveId, teamId).isPresent();
    }

    public void setAcceptance(SetObjectiveAcceptanceRequest request, UserAcc initiatorUserAcc) {
        Pair<Objective, Team> objectiveTeamPair = getExistingObjectiveAndTeam(request.getObjectiveId(), request.getTeamId());

        boolean isCurrentlyAccepted
                = objectiveAcceptanceRepository.findByObjectiveIdAndTeamId(request.getObjectiveId(), request.getTeamId()).isPresent();

        if (request.getWantedIsAccepted()) {
            if (isCurrentlyAccepted) {
                throw new ObjectiveAcceptanceException(ObjectiveAcceptanceApiError.OBJECTIVE_IS_ALREADY_ACCEPTED);
            }

            ObjectiveAcceptance newAcceptance = new ObjectiveAcceptance();
            newAcceptance.setObjective(objectiveTeamPair.getLeft());
            newAcceptance.setTeam(objectiveTeamPair.getRight());
            objectiveAcceptanceRepository.save(newAcceptance);
            log.info("ObjectiveAcceptance {} created by user {}.", newAcceptance.getId(), initiatorUserAcc.getId());
        } else {
            if (!isCurrentlyAccepted) {
                throw new ObjectiveAcceptanceException(ObjectiveAcceptanceApiError.OBJECTIVE_IS_ALREADY_NOT_ACCEPTED);
            }
            deleteTeamScore(objectiveTeamPair.getLeft(), objectiveTeamPair.getRight(), initiatorUserAcc);
        }
    }

    private Pair<Objective, Team> getExistingObjectiveAndTeam(Long objectiveId, Long teamId) {
        final Optional<Objective> objectiveOptional = objectiveRepository.findById(objectiveId);
        if (objectiveOptional.isEmpty()) {
            throw new ObjectiveAcceptanceException(ObjectiveAcceptanceApiError.OBJECTIVE_IS_NOT_FOUND);
        }
        final Optional<Team> teamOptional = teamRepository.findById(teamId);
        if (teamOptional.isEmpty()) {
            throw new ObjectiveAcceptanceException(ObjectiveAcceptanceApiError.TEAM_IS_NOT_FOUND);
        }

        return Pair.of(objectiveOptional.get(), teamOptional.get());
    }


    @Transactional
    public void deleteTeamScore(Objective objective, Team team, UserAcc deleterUserAcc) {
        int deletedRowCount;
        if ((deletedRowCount = objectiveAcceptanceRepository.deleteByObjectiveAndTeamAndGetDeletedRowCount(objective, team)) != 1) {
            throw new ContentNotFoundException("Count of deleted rows is " + deletedRowCount + "!");
        }

        log.info("ObjectiveAcceptance for Objective {} and Team {} deleted by user {}.", objective.getId(), team.getId(), deleterUserAcc.getId());
    }
}
