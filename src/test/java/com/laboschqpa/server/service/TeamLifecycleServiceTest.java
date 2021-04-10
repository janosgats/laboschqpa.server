package com.laboschqpa.server.service;

import com.laboschqpa.server.api.dto.team.CreateNewTeamRequest;
import com.laboschqpa.server.entity.Team;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.exceptions.apierrordescriptor.ContentNotFoundException;
import com.laboschqpa.server.repo.TeamRepository;
import com.laboschqpa.server.repo.UserAccRepository;
import com.laboschqpa.server.statemachine.StateMachineFactory;
import com.laboschqpa.server.statemachine.TeamLifecycleStateMachine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamLifecycleServiceTest {

    @Mock
    TeamLifecycleStateMachine teamLifecycleStateMachineMock;
    @Mock
    StateMachineFactory stateMachineFactoryMock;
    @Mock
    TransactionTemplate transactionTemplateMock;
    @Mock
    UserAccRepository userAccRepositoryMock;
    @Mock
    TeamRepository teamRepositoryMock;
    @InjectMocks
    TeamLifecycleService teamLifecycleService;

    @BeforeEach
    void setUp() {
        lenient().when(transactionTemplateMock.execute(argThat(TransactionCallbackWithoutResult.class::isInstance)))
                .then(transactionCallback -> ((TransactionCallbackWithoutResult) transactionCallback.getArgument(0)).doInTransaction(new SimpleTransactionStatus()));
        lenient().when(stateMachineFactoryMock.buildTeamUserRelationStateMachine(any(), any())).thenReturn(teamLifecycleStateMachineMock);
    }

    @Test
    void createNewTeam() {
        CreateNewTeamRequest createNewTeamRequest = spy(new CreateNewTeamRequest("    test name of team   "));
        Team createdTeam = new Team();
        Long creatorUserId = 12L;
        UserAcc creatorUser = UserAcc.builder().id(creatorUserId).enabled(true).team(createdTeam).build();
        when(transactionTemplateMock.execute(argThat(TransactionCallback.class::isInstance)))
                .then(transactionCallback -> ((TransactionCallback<Team>) transactionCallback.getArgument(0)).doInTransaction(new SimpleTransactionStatus()));

        when(userAccRepositoryMock.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(creatorUserId)).thenReturn(Optional.of(creatorUser));

        Team resultTeam = teamLifecycleService.createNewTeam(createNewTeamRequest, creatorUserId);

        assertEquals(createdTeam, resultTeam);

        verify(stateMachineFactoryMock, times(1)).buildTeamUserRelationStateMachine(creatorUser, creatorUser);
        verify(teamLifecycleStateMachineMock, times(1))
                .createNewTeam(argThat((a)
                        -> a.equals(createNewTeamRequest)
                        && a.getName().equals(createNewTeamRequest.getName().trim())
                ));

        verify(teamRepositoryMock, times(1)).save(creatorUser.getTeam());
        verify(userAccRepositoryMock, times(1)).save(creatorUser);
        verify(createNewTeamRequest, times(1)).validateSelf();
    }

    @Test
    void applyToTeam() {
        Team team = new Team(1L, "test", false);
        Long userAccId = 12L;
        UserAcc userAcc = UserAcc.builder().id(userAccId).enabled(true).team(team).build();

        when(userAccRepositoryMock.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(userAccId)).thenReturn(Optional.of(userAcc));
        when(teamRepositoryMock.findByIdAndArchivedIsFalse_WithPessimisticWriteLock(team.getId())).thenReturn(Optional.of(team));

        teamLifecycleService.applyToTeam(team.getId(), userAccId);

        verify(stateMachineFactoryMock, times(1)).buildTeamUserRelationStateMachine(userAcc, userAcc);
        verify(teamLifecycleStateMachineMock, times(1)).applyToTeam(team);

        verify(userAccRepositoryMock, times(1)).save(userAcc);
    }

    @Test
    void applyToTeam_TeamNotFound_ContentNotFoundApiException() {
        Team team = new Team(1L, "test", false);
        Long userAccId = 12L;
        UserAcc userAcc = UserAcc.builder().id(userAccId).enabled(true).team(team).build();

        when(userAccRepositoryMock.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(userAccId)).thenReturn(Optional.of(userAcc));
        when(teamRepositoryMock.findByIdAndArchivedIsFalse_WithPessimisticWriteLock(team.getId())).thenReturn(Optional.empty());

        assertThrows(ContentNotFoundException.class, () -> teamLifecycleService.applyToTeam(team.getId(), userAccId));

    }

    @Test
    void applyToTeam_UserNotFound_ContentNotFoundApiException() {
        Team team = new Team(1L, "test", false);
        Long userAccId = 12L;

        when(userAccRepositoryMock.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(userAccId)).thenReturn(Optional.empty());

        assertThrows(ContentNotFoundException.class, () -> teamLifecycleService.applyToTeam(team.getId(), userAccId));

    }

    @Test
    void cancelApplicationToTeam() {
        Long userAccId = 12L;
        UserAcc userAcc = UserAcc.builder().id(userAccId).enabled(true).build();

        when(userAccRepositoryMock.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(userAccId)).thenReturn(Optional.of(userAcc));

        teamLifecycleService.cancelApplicationToTeam(userAccId);

        verify(stateMachineFactoryMock, times(1)).buildTeamUserRelationStateMachine(userAcc, userAcc);
        verify(teamLifecycleStateMachineMock, times(1)).cancelApplicationToTeam();

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

        teamLifecycleService.declineApplicationToTeam(userAccIdToDecline, initiatorUserAccId);

        verify(stateMachineFactoryMock, times(1)).buildTeamUserRelationStateMachine(userAccToDecline, initiatorUserAcc);
        verify(teamLifecycleStateMachineMock, times(1)).declineApplicationToTeam();

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

        teamLifecycleService.approveApplicationToTeam(userAccIdToApprove, initiatorUserAccId);

        verify(stateMachineFactoryMock, times(1)).buildTeamUserRelationStateMachine(userAccToApprove, initiatorUserAcc);
        verify(teamLifecycleStateMachineMock, times(1)).approveApplication();

        verify(userAccRepositoryMock, times(1)).save(userAccToApprove);
    }

    @Test
    void leaveTeam() {
        Long userAccId = 12L;
        UserAcc userAcc = UserAcc.builder().id(userAccId).enabled(true).build();

        when(userAccRepositoryMock.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(userAccId)).thenReturn(Optional.of(userAcc));

        teamLifecycleService.leaveTeam(userAccId);

        verify(stateMachineFactoryMock, times(1)).buildTeamUserRelationStateMachine(userAcc, userAcc);
        verify(teamLifecycleStateMachineMock, times(1)).leaveTeam();

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

        teamLifecycleService.kickFromTeam(userAccIdToKick, initiatorUserAccId);

        verify(stateMachineFactoryMock, times(1)).buildTeamUserRelationStateMachine(userAccToKick, initiatorUserAcc);
        verify(teamLifecycleStateMachineMock, times(1)).kickFromTeam();

        verify(userAccRepositoryMock, times(1)).save(userAccToKick);
    }

    @Test
    void archiveAndLeaveTeam() {
        Long userAccId = 12L;
        UserAcc userAcc = UserAcc.builder().id(userAccId).enabled(true).team(new Team()).build();

        when(userAccRepositoryMock.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(userAccId)).thenReturn(Optional.of(userAcc));

        teamLifecycleService.archiveAndLeaveTeam(userAccId);

        verify(stateMachineFactoryMock, times(1)).buildTeamUserRelationStateMachine(userAcc, userAcc);
        verify(teamLifecycleStateMachineMock, times(1)).archiveAndLeaveTeam();

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

        teamLifecycleService.giveLeaderRights(userAccIdToGiveLeaderRights, initiatorUserAccId);

        verify(stateMachineFactoryMock, times(1)).buildTeamUserRelationStateMachine(userAccToGiveLeaderRights, initiatorUserAcc);
        verify(teamLifecycleStateMachineMock, times(1)).giveLeaderRights();

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

        teamLifecycleService.takeAwayLeaderRights(userAccIdToTakeAwayLeaderRights, initiatorUserAccId);

        verify(stateMachineFactoryMock, times(1)).buildTeamUserRelationStateMachine(userAccToTakeAwayLeaderRights, initiatorUserAcc);
        verify(teamLifecycleStateMachineMock, times(1)).takeAwayLeaderRights();

        verify(userAccRepositoryMock, times(1)).save(userAccToTakeAwayLeaderRights);
    }

    @Test
    void resignFromLeadership() {
        Long userAccId = 12L;
        UserAcc userAcc = UserAcc.builder().id(userAccId).enabled(true).build();

        when(userAccRepositoryMock.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(userAccId)).thenReturn(Optional.of(userAcc));

        teamLifecycleService.resignFromLeadership(userAccId);

        verify(stateMachineFactoryMock, times(1)).buildTeamUserRelationStateMachine(userAcc, userAcc);
        verify(teamLifecycleStateMachineMock, times(1)).resignFromLeadership();

        verify(userAccRepositoryMock, times(1)).save(userAcc);
    }
}