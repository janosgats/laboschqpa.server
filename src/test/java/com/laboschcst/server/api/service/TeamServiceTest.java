package com.laboschcst.server.api.service;

import com.laboschcst.server.api.dto.TeamDto;
import com.laboschcst.server.entity.Team;
import com.laboschcst.server.entity.account.UserAcc;
import com.laboschcst.server.exceptions.ContentNotFoundApiException;
import com.laboschcst.server.repo.Repos;
import com.laboschcst.server.repo.TeamRepository;
import com.laboschcst.server.repo.UserAccRepository;
import com.laboschcst.server.statemachine.StateMachineFactory;
import com.laboschcst.server.statemachine.TeamUserRelationTransitionsStateMachine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    TeamUserRelationTransitionsStateMachine teamUserRelationTransitionsStateMachineMock;
    @Mock
    StateMachineFactory stateMachineFactoryMock;
    @Mock
    UserAccRepository userAccRepositoryMock;
    @Mock
    TeamRepository teamRepositoryMock;
    @Mock
    TransactionTemplate transactionTemplateMock;

    TeamService teamService;

    Repos repos;

    @BeforeEach
    void setUp() {
        repos = Repos.builder().userAccRepository(userAccRepositoryMock).teamRepository(teamRepositoryMock).build();
        teamService = new TeamService(repos, transactionTemplateMock, stateMachineFactoryMock);

        lenient().when(transactionTemplateMock.execute(any())).then(transactionCallback -> ((TransactionCallbackWithoutResult) transactionCallback.getArgument(0)).doInTransaction(new SimpleTransactionStatus()));
        lenient().when(stateMachineFactoryMock.buildTeamUserRelationTransitionsStateMachine(any(), any())).thenReturn(teamUserRelationTransitionsStateMachineMock);
    }

    @Test
    void createNewTeam() {
        TeamDto teamDto = new TeamDto(1L, "test");
        Long creatorUserId = 12L;
        UserAcc creatorUser = UserAcc.builder().id(creatorUserId).enabled(true).team(new Team()).build();

        when(userAccRepositoryMock.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(creatorUserId)).thenReturn(Optional.of(creatorUser));

        teamService.createNewTeam(teamDto, creatorUserId);

        verify(stateMachineFactoryMock, times(1)).buildTeamUserRelationTransitionsStateMachine(creatorUser, creatorUser);
        verify(teamUserRelationTransitionsStateMachineMock, times(1)).createNewTeam(teamDto);

        verify(teamRepositoryMock, times(1)).save(creatorUser.getTeam());
        verify(userAccRepositoryMock, times(1)).save(creatorUser);
    }

    @Test
    void applyToTeam() {
        Team team = new Team(1L, "test", false);
        Long userAccId = 12L;
        UserAcc userAcc = UserAcc.builder().id(userAccId).enabled(true).team(team).build();

        when(userAccRepositoryMock.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(userAccId)).thenReturn(Optional.of(userAcc));
        when(teamRepositoryMock.findByIdAndArchivedIsFalse_WithPessimisticWriteLock(team.getId())).thenReturn(Optional.of(team));

        teamService.applyToTeam(team.getId(), userAccId);

        verify(stateMachineFactoryMock, times(1)).buildTeamUserRelationTransitionsStateMachine(userAcc, userAcc);
        verify(teamUserRelationTransitionsStateMachineMock, times(1)).applyToTeam(team);

        verify(userAccRepositoryMock, times(1)).save(userAcc);
    }

    @Test
    void applyToTeam_TeamNotFound_ContentNotFoundApiException() {
        Team team = new Team(1L, "test", false);
        Long userAccId = 12L;
        UserAcc userAcc = UserAcc.builder().id(userAccId).enabled(true).team(team).build();

        when(userAccRepositoryMock.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(userAccId)).thenReturn(Optional.of(userAcc));
        when(teamRepositoryMock.findByIdAndArchivedIsFalse_WithPessimisticWriteLock(team.getId())).thenReturn(Optional.empty());

        assertThrows(ContentNotFoundApiException.class, () -> teamService.applyToTeam(team.getId(), userAccId));

    }

    @Test
    void applyToTeam_UserNotFound_ContentNotFoundApiException() {
        Team team = new Team(1L, "test", false);
        Long userAccId = 12L;

        when(userAccRepositoryMock.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(userAccId)).thenReturn(Optional.empty());

        assertThrows(ContentNotFoundApiException.class, () -> teamService.applyToTeam(team.getId(), userAccId));

    }

    @Test
    void cancelApplicationToTeam() {
        Long userAccId = 12L;
        UserAcc userAcc = UserAcc.builder().id(userAccId).enabled(true).build();

        when(userAccRepositoryMock.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(userAccId)).thenReturn(Optional.of(userAcc));

        teamService.cancelApplicationToTeam(userAccId);

        verify(stateMachineFactoryMock, times(1)).buildTeamUserRelationTransitionsStateMachine(userAcc, userAcc);
        verify(teamUserRelationTransitionsStateMachineMock, times(1)).cancelApplicationToTeam();

        verify(userAccRepositoryMock, times(1)).save(userAcc);
    }

    @Test
    void declineApplicationToTeam() {
        Long userAccIdToDecline = 12L;
        Long initiatorUserAccId = 31L;
        UserAcc userAccToDecline = UserAcc.builder().id(userAccIdToDecline).enabled(true).build();
        UserAcc initiatorUserAcc = UserAcc.builder().id(initiatorUserAccId).enabled(true).build();

        when(userAccRepositoryMock.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(userAccIdToDecline)).thenReturn(Optional.of(userAccToDecline));
        when(userAccRepositoryMock.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(initiatorUserAccId)).thenReturn(Optional.of(initiatorUserAcc));

        teamService.declineApplicationToTeam(userAccIdToDecline, initiatorUserAccId);

        verify(stateMachineFactoryMock, times(1)).buildTeamUserRelationTransitionsStateMachine(userAccToDecline, initiatorUserAcc);
        verify(teamUserRelationTransitionsStateMachineMock, times(1)).declineApplicationToTeam();

        verify(userAccRepositoryMock, times(1)).save(userAccToDecline);
    }

    @Test
    void approveApplicationToTeam() {
        Long userAccIdToApprove = 12L;
        Long initiatorUserAccId = 31L;
        UserAcc userAccToApprove = UserAcc.builder().id(userAccIdToApprove).enabled(true).build();
        UserAcc initiatorUserAcc = UserAcc.builder().id(initiatorUserAccId).enabled(true).build();

        when(userAccRepositoryMock.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(userAccIdToApprove)).thenReturn(Optional.of(userAccToApprove));
        when(userAccRepositoryMock.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(initiatorUserAccId)).thenReturn(Optional.of(initiatorUserAcc));

        teamService.approveApplicationToTeam(userAccIdToApprove, initiatorUserAccId);

        verify(stateMachineFactoryMock, times(1)).buildTeamUserRelationTransitionsStateMachine(userAccToApprove, initiatorUserAcc);
        verify(teamUserRelationTransitionsStateMachineMock, times(1)).approveApplication();

        verify(userAccRepositoryMock, times(1)).save(userAccToApprove);
    }

    @Test
    void leaveTeam() {
        Long userAccId = 12L;
        UserAcc userAcc = UserAcc.builder().id(userAccId).enabled(true).build();

        when(userAccRepositoryMock.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(userAccId)).thenReturn(Optional.of(userAcc));

        teamService.leaveTeam(userAccId);

        verify(stateMachineFactoryMock, times(1)).buildTeamUserRelationTransitionsStateMachine(userAcc, userAcc);
        verify(teamUserRelationTransitionsStateMachineMock, times(1)).leaveTeam();

        verify(userAccRepositoryMock, times(1)).save(userAcc);
    }

    @Test
    void kickFromTeam() {
        Long userAccIdToKick = 12L;
        Long initiatorUserAccId = 31L;
        UserAcc userAccToKick = UserAcc.builder().id(userAccIdToKick).enabled(true).build();
        UserAcc initiatorUserAcc = UserAcc.builder().id(initiatorUserAccId).enabled(true).build();

        when(userAccRepositoryMock.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(userAccIdToKick)).thenReturn(Optional.of(userAccToKick));
        when(userAccRepositoryMock.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(initiatorUserAccId)).thenReturn(Optional.of(initiatorUserAcc));

        teamService.kickFromTeam(userAccIdToKick, initiatorUserAccId);

        verify(stateMachineFactoryMock, times(1)).buildTeamUserRelationTransitionsStateMachine(userAccToKick, initiatorUserAcc);
        verify(teamUserRelationTransitionsStateMachineMock, times(1)).kickFromTeam();

        verify(userAccRepositoryMock, times(1)).save(userAccToKick);
    }

    @Test
    void archiveAndLeaveTeam() {
        Long userAccId = 12L;
        UserAcc userAcc = UserAcc.builder().id(userAccId).enabled(true).team(new Team()).build();

        when(userAccRepositoryMock.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(userAccId)).thenReturn(Optional.of(userAcc));

        teamService.archiveAndLeaveTeam(userAccId);

        verify(stateMachineFactoryMock, times(1)).buildTeamUserRelationTransitionsStateMachine(userAcc, userAcc);
        verify(teamUserRelationTransitionsStateMachineMock, times(1)).archiveAndLeaveTeam();

        verify(teamRepositoryMock, times(1)).save(userAcc.getTeam());
        verify(userAccRepositoryMock, times(1)).save(userAcc);
    }

    @Test
    void giveLeaderRights() {
        Long userAccIdToGiveLeaderRights = 12L;
        Long initiatorUserAccId = 31L;
        UserAcc userAccToGiveLeaderRights = UserAcc.builder().id(userAccIdToGiveLeaderRights).enabled(true).build();
        UserAcc initiatorUserAcc = UserAcc.builder().id(initiatorUserAccId).enabled(true).build();

        when(userAccRepositoryMock.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(userAccIdToGiveLeaderRights)).thenReturn(Optional.of(userAccToGiveLeaderRights));
        when(userAccRepositoryMock.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(initiatorUserAccId)).thenReturn(Optional.of(initiatorUserAcc));

        teamService.giveLeaderRights(userAccIdToGiveLeaderRights, initiatorUserAccId);

        verify(stateMachineFactoryMock, times(1)).buildTeamUserRelationTransitionsStateMachine(userAccToGiveLeaderRights, initiatorUserAcc);
        verify(teamUserRelationTransitionsStateMachineMock, times(1)).giveLeaderRights();

        verify(userAccRepositoryMock, times(1)).save(userAccToGiveLeaderRights);
    }

    @Test
    void takeAwayLeaderRights() {
        Long userAccIdToTakeAwayLeaderRights = 12L;
        Long initiatorUserAccId = 31L;
        UserAcc userAccToTakeAwayLeaderRights = UserAcc.builder().id(userAccIdToTakeAwayLeaderRights).enabled(true).build();
        UserAcc initiatorUserAcc = UserAcc.builder().id(initiatorUserAccId).enabled(true).build();

        when(userAccRepositoryMock.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(userAccIdToTakeAwayLeaderRights)).thenReturn(Optional.of(userAccToTakeAwayLeaderRights));
        when(userAccRepositoryMock.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(initiatorUserAccId)).thenReturn(Optional.of(initiatorUserAcc));

        teamService.takeAwayLeaderRights(userAccIdToTakeAwayLeaderRights, initiatorUserAccId);

        verify(stateMachineFactoryMock, times(1)).buildTeamUserRelationTransitionsStateMachine(userAccToTakeAwayLeaderRights, initiatorUserAcc);
        verify(teamUserRelationTransitionsStateMachineMock, times(1)).takeAwayLeaderRights();

        verify(userAccRepositoryMock, times(1)).save(userAccToTakeAwayLeaderRights);
    }

    @Test
    void resignFromLeadership() {
        Long userAccId = 12L;
        UserAcc userAcc = UserAcc.builder().id(userAccId).enabled(true).build();

        when(userAccRepositoryMock.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(userAccId)).thenReturn(Optional.of(userAcc));

        teamService.resignFromLeadership(userAccId);

        verify(stateMachineFactoryMock, times(1)).buildTeamUserRelationTransitionsStateMachine(userAcc, userAcc);
        verify(teamUserRelationTransitionsStateMachineMock, times(1)).resignFromLeadership();

        verify(userAccRepositoryMock, times(1)).save(userAcc);
    }
}