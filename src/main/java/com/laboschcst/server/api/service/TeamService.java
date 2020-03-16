package com.laboschcst.server.api.service;

import com.laboschcst.server.api.dto.TeamDto;
import com.laboschcst.server.api.validator.TeamValidator;
import com.laboschcst.server.entity.Team;
import com.laboschcst.server.entity.account.UserAcc;
import com.laboschcst.server.exceptions.ContentNotFoundApiException;
import com.laboschcst.server.repo.Repos;
import com.laboschcst.server.statemachine.StateMachineFactory;
import com.laboschcst.server.statemachine.TeamUserRelationStateMachine;
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
    private final StateMachineFactory stateMachineFactory;

    public void createNewTeam(TeamDto teamDto, Long creatorUserId) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            public void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                new TeamValidator(teamDto);

                UserAcc userAcc = readEnabledUserAccFromDbWithPessimisticLock(creatorUserId);

                TeamUserRelationStateMachine stateMachine = stateMachineFactory.buildTeamUserRelationStateMachine(userAcc, userAcc);
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

                TeamUserRelationStateMachine stateMachine = stateMachineFactory.buildTeamUserRelationStateMachine(userAcc, userAcc);
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

                TeamUserRelationStateMachine stateMachine = stateMachineFactory.buildTeamUserRelationStateMachine(userAccToCancel, userAccToCancel);
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

                TeamUserRelationStateMachine stateMachine = stateMachineFactory.buildTeamUserRelationStateMachine(userAccToDecline, initiatorUserAcc);
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

                TeamUserRelationStateMachine stateMachine = stateMachineFactory.buildTeamUserRelationStateMachine(userAccToApprove, initiatorUserAcc);
                stateMachine.approveApplication();

                repos.userAccRepository.save(userAccToApprove);
            }
        });
    }

    public void leaveTeam(Long userAccId) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            public void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                UserAcc userAcc = readEnabledUserAccFromDbWithPessimisticLock(userAccId);

                TeamUserRelationStateMachine stateMachine = stateMachineFactory.buildTeamUserRelationStateMachine(userAcc, userAcc);
                stateMachine.leaveTeam();

                repos.userAccRepository.save(userAcc);
            }
        });
    }


    public void kickFromTeam(Long userAccIdToKick, Long initiatorUserAccId) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            public void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                UserAcc userAccToKick = readEnabledUserAccFromDbWithPessimisticLock(userAccIdToKick);
                UserAcc initiatorUserAcc = readEnabledUserAccFromDbWithPessimisticLock(initiatorUserAccId);

                TeamUserRelationStateMachine stateMachine = stateMachineFactory.buildTeamUserRelationStateMachine(userAccToKick, initiatorUserAcc);
                stateMachine.kickFromTeam();

                repos.userAccRepository.save(userAccToKick);
            }
        });
    }

    public void archiveAndLeaveTeam(Long userAccId) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            public void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                UserAcc userAcc = readEnabledUserAccFromDbWithPessimisticLock(userAccId);

                Team teamToArchive = userAcc.getTeam();

                TeamUserRelationStateMachine stateMachine = stateMachineFactory.buildTeamUserRelationStateMachine(userAcc, userAcc);
                stateMachine.archiveAndLeaveTeam();

                repos.teamRepository.save(teamToArchive);
                repos.userAccRepository.save(userAcc);
            }
        });
    }

    public void giveLeaderRights(Long userAccIdToGiveLeaderRights, Long initiatorUserAccId) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            public void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                UserAcc userAccToGiveLeaderRights = readEnabledUserAccFromDbWithPessimisticLock(userAccIdToGiveLeaderRights);
                UserAcc initiatorUserAcc = readEnabledUserAccFromDbWithPessimisticLock(initiatorUserAccId);

                TeamUserRelationStateMachine stateMachine = stateMachineFactory.buildTeamUserRelationStateMachine(userAccToGiveLeaderRights, initiatorUserAcc);
                stateMachine.giveLeaderRights();

                repos.userAccRepository.save(userAccToGiveLeaderRights);
            }
        });
    }

    public void takeAwayLeaderRights(Long userAccIdToTakeAwayLeaderRights, Long initiatorUserAccId) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            public void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                UserAcc userAccToTakeAwayLeaderRights = readEnabledUserAccFromDbWithPessimisticLock(userAccIdToTakeAwayLeaderRights);
                UserAcc initiatorUserAcc = readEnabledUserAccFromDbWithPessimisticLock(initiatorUserAccId);

                TeamUserRelationStateMachine stateMachine = stateMachineFactory.buildTeamUserRelationStateMachine(userAccToTakeAwayLeaderRights, initiatorUserAcc);
                stateMachine.takeAwayLeaderRights();

                repos.userAccRepository.save(userAccToTakeAwayLeaderRights);
            }
        });
    }

    public void resignFromLeadership(Long userAccId) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            public void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                UserAcc userAcc = readEnabledUserAccFromDbWithPessimisticLock(userAccId);

                TeamUserRelationStateMachine stateMachine = stateMachineFactory.buildTeamUserRelationStateMachine(userAcc, userAcc);
                stateMachine.resignFromLeadership();

                repos.userAccRepository.save(userAcc);
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
