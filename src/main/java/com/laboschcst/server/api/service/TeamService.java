package com.laboschcst.server.api.service;

import com.laboschcst.server.api.dto.TeamDto;
import com.laboschcst.server.api.validator.TeamValidator;
import com.laboschcst.server.entity.Team;
import com.laboschcst.server.entity.account.UserAcc;
import com.laboschcst.server.exceptions.ContentNotFoundApiException;
import com.laboschcst.server.repo.Repos;
import com.laboschcst.server.statemachine.TeamUserRelationTransitionsStateMachine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final Repos repos;
    private final TransactionTemplate transactionTemplate;

    public void createNewTeam(TeamDto teamDto, Long creatorUserId) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            public void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                new TeamValidator(teamDto);

                UserAcc userAcc = readEnabledUserAccFromDbWithPessimisticLock(creatorUserId);

                TeamUserRelationTransitionsStateMachine stateMachine = new TeamUserRelationTransitionsStateMachine(userAcc, userAcc);
                stateMachine.createNewTeam(teamDto);

                repos.teamRepository.save(userAcc.getTeam());
                repos.userAccRepository.save(userAcc);
            }
        });
    }

    public void applyToTeam(Long teamId, Long userAccId) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            public void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                UserAcc userAcc = readEnabledUserAccFromDbWithPessimisticLock(userAccId);
                Team team = readNotArchivedTeamFromDbWithPessimisticLock(teamId);

                TeamUserRelationTransitionsStateMachine stateMachine = new TeamUserRelationTransitionsStateMachine(userAcc, userAcc);
                stateMachine.applyToTeam(team);

                repos.userAccRepository.save(userAcc);
            }
        });
    }

    /**
     * Canceling own application.
     */
    public void cancelApplicationToTeam(Long userAccIdToCancel) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            public void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                UserAcc userAccToCancel = readEnabledUserAccFromDbWithPessimisticLock(userAccIdToCancel);

                TeamUserRelationTransitionsStateMachine stateMachine = new TeamUserRelationTransitionsStateMachine(userAccToCancel, userAccToCancel);
                stateMachine.cancelApplicationToTeam();

                repos.userAccRepository.save(userAccToCancel);
            }
        });
    }

    public void declineApplicationToTeam(Long userAccIdToDecline, Long initiatorUserAccId) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            public void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                UserAcc userAccToDecline = readEnabledUserAccFromDbWithPessimisticLock(userAccIdToDecline);
                UserAcc initiatorUserAcc = readEnabledUserAccFromDbWithPessimisticLock(initiatorUserAccId);

                TeamUserRelationTransitionsStateMachine stateMachine = new TeamUserRelationTransitionsStateMachine(userAccToDecline, initiatorUserAcc);
                stateMachine.declineApplicationToTeam();

                repos.userAccRepository.save(userAccToDecline);
            }
        });
    }

    public void approveApplicationToTeam(Long userAccIdToApprove, Long initiatorUserAccId) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            public void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                UserAcc userAccToApprove = readEnabledUserAccFromDbWithPessimisticLock(userAccIdToApprove);
                UserAcc initiatorUserAcc = readEnabledUserAccFromDbWithPessimisticLock(initiatorUserAccId);

                TeamUserRelationTransitionsStateMachine stateMachine = new TeamUserRelationTransitionsStateMachine(userAccToApprove, initiatorUserAcc);
                stateMachine.approveApplication();

                repos.userAccRepository.save(userAccToApprove);
            }
        });
    }

    private UserAcc readEnabledUserAccFromDbWithPessimisticLock(Long userAccId) {
        Optional<UserAcc> userAccOptional = repos.userAccRepository.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(userAccId);
        if (userAccOptional.isEmpty())
            throw new ContentNotFoundApiException("UserAcc is not found or not enabled.");
        return userAccOptional.get();
    }

    private Team readNotArchivedTeamFromDbWithPessimisticLock(Long userAccId) {
        Optional<Team> teamOptional = repos.teamRepository.findByIdAndArchivedIsFalse_WithPessimisticWriteLock(userAccId);
        if (teamOptional.isEmpty())
            throw new ContentNotFoundApiException("Team is not found or is archived.");
        return teamOptional.get();
    }
}
