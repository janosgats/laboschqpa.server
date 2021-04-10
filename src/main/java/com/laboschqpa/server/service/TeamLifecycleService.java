package com.laboschqpa.server.service;

import com.laboschqpa.server.api.dto.team.CreateNewTeamRequest;
import com.laboschqpa.server.entity.Team;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.exceptions.apierrordescriptor.ContentNotFoundException;
import com.laboschqpa.server.repo.TeamRepository;
import com.laboschqpa.server.repo.UserAccRepository;
import com.laboschqpa.server.statemachine.StateMachineFactory;
import com.laboschqpa.server.statemachine.TeamLifecycleStateMachine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamLifecycleService {
    private final UserAccRepository userAccRepository;
    private final TeamRepository teamRepository;
    private final TransactionTemplate transactionTemplate;
    private final StateMachineFactory stateMachineFactory;

    /**
     * @return The newly created {@link Team} entity.
     */
    public Team createNewTeam(CreateNewTeamRequest createNewTeamRequest, Long creatorUserId) {
        createNewTeamRequest.validateSelf();
        return transactionTemplate.execute(transactionStatus -> {
            createNewTeamRequest.setName(createNewTeamRequest.getName().trim());

            UserAcc userAcc = readEnabledUserAccFromDbWithPessimisticLock(creatorUserId);

            TeamLifecycleStateMachine stateMachine = stateMachineFactory.buildTeamUserRelationStateMachine(userAcc, userAcc);
            stateMachine.createNewTeam(createNewTeamRequest);

            teamRepository.save(userAcc.getTeam());
            userAccRepository.save(userAcc);

            return userAcc.getTeam();
        });
    }

    public void applyToTeam(Long teamId, Long userAccId) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            UserAcc userAcc = readEnabledUserAccFromDbWithPessimisticLock(userAccId);
            Team team = readNotArchivedTeamFromDbWithPessimisticLock(teamId);

            TeamLifecycleStateMachine stateMachine = stateMachineFactory.buildTeamUserRelationStateMachine(userAcc, userAcc);
            stateMachine.applyToTeam(team);

            userAccRepository.save(userAcc);
        });
    }

    /**
     * Canceling own application.
     */
    public void cancelApplicationToTeam(Long userAccIdToCancel) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            UserAcc userAccToCancel = readEnabledUserAccFromDbWithPessimisticLock(userAccIdToCancel);

            TeamLifecycleStateMachine stateMachine = stateMachineFactory.buildTeamUserRelationStateMachine(userAccToCancel, userAccToCancel);
            stateMachine.cancelApplicationToTeam();

            userAccRepository.save(userAccToCancel);
        });
    }

    public void declineApplicationToTeam(Long userAccIdToDecline, Long initiatorUserAccId) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            UserAcc userAccToDecline = readEnabledUserAccFromDbWithPessimisticLock(userAccIdToDecline);
            UserAcc initiatorUserAcc = readEnabledUserAccFromDbWithPessimisticLock(initiatorUserAccId);

            TeamLifecycleStateMachine stateMachine = stateMachineFactory.buildTeamUserRelationStateMachine(userAccToDecline, initiatorUserAcc);
            stateMachine.declineApplicationToTeam();

            userAccRepository.save(userAccToDecline);
        });
    }

    public void approveApplicationToTeam(Long userAccIdToApprove, Long initiatorUserAccId) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            UserAcc userAccToApprove = readEnabledUserAccFromDbWithPessimisticLock(userAccIdToApprove);
            UserAcc initiatorUserAcc = readEnabledUserAccFromDbWithPessimisticLock(initiatorUserAccId);

            TeamLifecycleStateMachine stateMachine = stateMachineFactory.buildTeamUserRelationStateMachine(userAccToApprove, initiatorUserAcc);
            stateMachine.approveApplication();

            userAccRepository.save(userAccToApprove);
        });
    }

    public void leaveTeam(Long userAccId) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            UserAcc userAcc = readEnabledUserAccFromDbWithPessimisticLock(userAccId);

            TeamLifecycleStateMachine stateMachine = stateMachineFactory.buildTeamUserRelationStateMachine(userAcc, userAcc);
            stateMachine.leaveTeam();

            userAccRepository.save(userAcc);
        });
    }


    public void kickFromTeam(Long userAccIdToKick, Long initiatorUserAccId) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            UserAcc userAccToKick = readEnabledUserAccFromDbWithPessimisticLock(userAccIdToKick);
            UserAcc initiatorUserAcc = readEnabledUserAccFromDbWithPessimisticLock(initiatorUserAccId);

            TeamLifecycleStateMachine stateMachine = stateMachineFactory.buildTeamUserRelationStateMachine(userAccToKick, initiatorUserAcc);
            stateMachine.kickFromTeam();

            userAccRepository.save(userAccToKick);
        });
    }

    public void archiveAndLeaveTeam(Long userAccId) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            UserAcc userAcc = readEnabledUserAccFromDbWithPessimisticLock(userAccId);

            Team teamToArchive = userAcc.getTeam();

            TeamLifecycleStateMachine stateMachine = stateMachineFactory.buildTeamUserRelationStateMachine(userAcc, userAcc);
            stateMachine.archiveAndLeaveTeam();

            teamRepository.save(teamToArchive);
            userAccRepository.save(userAcc);
        });
    }

    public void giveLeaderRights(Long userAccIdToGiveLeaderRights, Long initiatorUserAccId) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            UserAcc userAccToGiveLeaderRights = readEnabledUserAccFromDbWithPessimisticLock(userAccIdToGiveLeaderRights);
            UserAcc initiatorUserAcc = readEnabledUserAccFromDbWithPessimisticLock(initiatorUserAccId);

            TeamLifecycleStateMachine stateMachine = stateMachineFactory.buildTeamUserRelationStateMachine(userAccToGiveLeaderRights, initiatorUserAcc);
            stateMachine.giveLeaderRights();

            userAccRepository.save(userAccToGiveLeaderRights);
        });
    }

    public void takeAwayLeaderRights(Long userAccIdToTakeAwayLeaderRights, Long initiatorUserAccId) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            UserAcc userAccToTakeAwayLeaderRights = readEnabledUserAccFromDbWithPessimisticLock(userAccIdToTakeAwayLeaderRights);
            UserAcc initiatorUserAcc = readEnabledUserAccFromDbWithPessimisticLock(initiatorUserAccId);

            TeamLifecycleStateMachine stateMachine = stateMachineFactory.buildTeamUserRelationStateMachine(userAccToTakeAwayLeaderRights, initiatorUserAcc);
            stateMachine.takeAwayLeaderRights();

            userAccRepository.save(userAccToTakeAwayLeaderRights);
        });
    }

    public void resignFromLeadership(Long userAccId) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            UserAcc userAcc = readEnabledUserAccFromDbWithPessimisticLock(userAccId);

            TeamLifecycleStateMachine stateMachine = stateMachineFactory.buildTeamUserRelationStateMachine(userAcc, userAcc);
            stateMachine.resignFromLeadership();

            userAccRepository.save(userAcc);
        });
    }

    private UserAcc readEnabledUserAccFromDbWithPessimisticLock(Long userAccId) {
        Optional<UserAcc> userAccOptional = userAccRepository.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(userAccId);
        if (userAccOptional.isEmpty())
            throw new ContentNotFoundException("UserAcc is not found or not enabled.");
        return userAccOptional.get();
    }

    private Team readNotArchivedTeamFromDbWithPessimisticLock(Long userAccId) {
        Optional<Team> teamOptional = teamRepository.findByIdAndArchivedIsFalse_WithPessimisticWriteLock(userAccId);
        if (teamOptional.isEmpty())
            throw new ContentNotFoundException("Team is not found or is archived.");
        return teamOptional.get();
    }
}
