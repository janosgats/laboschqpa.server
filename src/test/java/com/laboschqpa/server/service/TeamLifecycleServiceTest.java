package com.laboschqpa.server.service;

import com.laboschqpa.server.api.dto.team.CreateNewTeamRequest;
import com.laboschqpa.server.api.service.event.registration.TeamEventRegistrationService;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamLifecycleServiceTest {

    @Mock
    TeamLifecycleStateMachine teamLifecycleStateMachine;
    @Mock
    StateMachineFactory stateMachineFactory;
    @Mock
    TransactionTemplate transactionTemplate;
    @Mock
    UserAccRepository userAccRepository;
    @Mock
    TeamRepository teamRepository;
    @Mock
    TeamEventRegistrationService teamEventRegistrationService;
    @InjectMocks
    TeamLifecycleService teamLifecycleService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(teamLifecycleService,
                "disableNewTeamCreation", false);

        lenient().when(transactionTemplate.execute(any()))
                .then(invocationOnMock -> ((TransactionCallback) (invocationOnMock.getArgument(0))).doInTransaction(new SimpleTransactionStatus()));
        lenient().doCallRealMethod()
                .when(transactionTemplate).executeWithoutResult(any());
        lenient().when(stateMachineFactory.buildTeamLifecycleStateMachine(any(), any())).thenReturn(teamLifecycleStateMachine);
    }

    @Test
    void createNewTeam() {
        CreateNewTeamRequest createNewTeamRequest = spy(new CreateNewTeamRequest("    test name of team   "));
        Team createdTeam = new Team();
        Long creatorUserId = 12L;
        UserAcc creatorUser = UserAcc.builder().id(creatorUserId).enabled(true).team(createdTeam).build();

        when(userAccRepository.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(creatorUserId)).thenReturn(Optional.of(creatorUser));

        Team resultTeam = teamLifecycleService.createNewTeam(createNewTeamRequest, creatorUserId);

        assertEquals(createdTeam, resultTeam);

        verify(stateMachineFactory, times(1)).buildTeamLifecycleStateMachine(creatorUser, creatorUser);
        verify(teamLifecycleStateMachine, times(1))
                .createNewTeam(argThat((a)
                        -> a.equals(createNewTeamRequest)
                        && a.getName().equals(createNewTeamRequest.getName().trim())
                ));

        verify(teamRepository, times(1)).save(creatorUser.getTeam());
        verify(userAccRepository, times(1)).save(creatorUser);
        verify(createNewTeamRequest, times(1)).validateSelf();
    }

    @Test
    void applyToTeam() {
        Team team = new Team(1L, "test", false, false, true);
        Long userAccId = 12L;
        UserAcc userAcc = UserAcc.builder().id(userAccId).enabled(true).team(team).build();

        when(userAccRepository.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(userAccId)).thenReturn(Optional.of(userAcc));
        when(teamRepository.findByIdAndArchivedIsFalse_WithPessimisticWriteLock(team.getId())).thenReturn(Optional.of(team));

        teamLifecycleService.applyToTeam(team.getId(), userAccId);

        verify(stateMachineFactory, times(1)).buildTeamLifecycleStateMachine(userAcc, userAcc);
        verify(teamLifecycleStateMachine, times(1)).applyToTeam(team);

        verify(userAccRepository, times(1)).save(userAcc);
    }

    @Test
    void applyToTeam_TeamNotFound_ContentNotFoundApiException() {
        Team team = new Team(1L, "test", false, false, true);
        Long userAccId = 12L;
        UserAcc userAcc = UserAcc.builder().id(userAccId).enabled(true).team(team).build();

        when(userAccRepository.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(userAccId)).thenReturn(Optional.of(userAcc));
        when(teamRepository.findByIdAndArchivedIsFalse_WithPessimisticWriteLock(team.getId())).thenReturn(Optional.empty());

        assertThrows(ContentNotFoundException.class, () -> teamLifecycleService.applyToTeam(team.getId(), userAccId));

    }

    @Test
    void applyToTeam_UserNotFound_ContentNotFoundApiException() {
        Team team = new Team(1L, "test", false, false, true);
        Long userAccId = 12L;

        when(userAccRepository.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(userAccId)).thenReturn(Optional.empty());

        assertThrows(ContentNotFoundException.class, () -> teamLifecycleService.applyToTeam(team.getId(), userAccId));

    }

    @Test
    void cancelApplicationToTeam() {
        Long userAccId = 12L;
        UserAcc userAcc = UserAcc.builder().id(userAccId).enabled(true).build();

        when(userAccRepository.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(userAccId)).thenReturn(Optional.of(userAcc));

        teamLifecycleService.cancelApplicationToTeam(userAccId);

        verify(stateMachineFactory, times(1)).buildTeamLifecycleStateMachine(userAcc, userAcc);
        verify(teamLifecycleStateMachine, times(1)).cancelApplicationToTeam();

        verify(userAccRepository, times(1)).save(userAcc);
    }

    @Test
    void declineApplicationToTeam() {
        Long userAccIdToDecline = 12L;
        Long initiatorUserAccId = 31L;
        UserAcc userAccToDecline = UserAcc.builder().id(userAccIdToDecline).enabled(true).build();
        UserAcc initiatorUserAcc = UserAcc.builder().id(initiatorUserAccId).enabled(true).build();

        when(userAccRepository.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(userAccIdToDecline)).thenReturn(Optional.of(userAccToDecline));
        when(userAccRepository.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(initiatorUserAccId)).thenReturn(Optional.of(initiatorUserAcc));

        teamLifecycleService.declineApplicationToTeam(userAccIdToDecline, initiatorUserAccId);

        verify(stateMachineFactory, times(1)).buildTeamLifecycleStateMachine(userAccToDecline, initiatorUserAcc);
        verify(teamLifecycleStateMachine, times(1)).declineApplicationToTeam();

        verify(userAccRepository, times(1)).save(userAccToDecline);
    }

    @Test
    void approveApplicationToTeam() {
        Long userAccIdToApprove = 12L;
        Long initiatorUserAccId = 31L;
        UserAcc userAccToApprove = UserAcc.builder().id(userAccIdToApprove).enabled(true).build();
        UserAcc initiatorUserAcc = UserAcc.builder().id(initiatorUserAccId).enabled(true).build();

        when(userAccRepository.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(userAccIdToApprove)).thenReturn(Optional.of(userAccToApprove));
        when(userAccRepository.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(initiatorUserAccId)).thenReturn(Optional.of(initiatorUserAcc));

        teamLifecycleService.approveApplicationToTeam(userAccIdToApprove, initiatorUserAccId);

        verify(stateMachineFactory, times(1)).buildTeamLifecycleStateMachine(userAccToApprove, initiatorUserAcc);
        verify(teamLifecycleStateMachine, times(1)).approveApplication();

        verify(userAccRepository, times(1)).save(userAccToApprove);
    }

    @Test
    void leaveTeam() {
        Long userAccId = 12L;
        UserAcc userAcc = UserAcc.builder().id(userAccId).enabled(true).build();

        when(userAccRepository.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(userAccId)).thenReturn(Optional.of(userAcc));

        teamLifecycleService.leaveTeam(userAccId);

        verify(stateMachineFactory, times(1)).buildTeamLifecycleStateMachine(userAcc, userAcc);
        verify(teamLifecycleStateMachine, times(1)).leaveTeam();

        verify(userAccRepository, times(1)).save(userAcc);
    }

    @Test
    void kickFromTeam() {
        Long userAccIdToKick = 12L;
        Long initiatorUserAccId = 31L;
        UserAcc userAccToKick = UserAcc.builder().id(userAccIdToKick).enabled(true).build();
        UserAcc initiatorUserAcc = UserAcc.builder().id(initiatorUserAccId).enabled(true).build();

        when(userAccRepository.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(userAccIdToKick)).thenReturn(Optional.of(userAccToKick));
        when(userAccRepository.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(initiatorUserAccId)).thenReturn(Optional.of(initiatorUserAcc));

        teamLifecycleService.kickFromTeam(userAccIdToKick, initiatorUserAccId);

        verify(stateMachineFactory, times(1)).buildTeamLifecycleStateMachine(userAccToKick, initiatorUserAcc);
        verify(teamLifecycleStateMachine, times(1)).kickFromTeam();

        verify(userAccRepository, times(1)).save(userAccToKick);
    }

    @Test
    void archiveAndLeaveTeam() {
        Long userAccId = 12L;
        UserAcc userAcc = UserAcc.builder().id(userAccId).enabled(true).team(new Team()).build();

        when(userAccRepository.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(userAccId)).thenReturn(Optional.of(userAcc));

        teamLifecycleService.archiveAndLeaveTeam(userAccId);

        verify(stateMachineFactory, times(1)).buildTeamLifecycleStateMachine(userAcc, userAcc);
        verify(teamLifecycleStateMachine, times(1)).archiveAndLeaveTeam();

        verify(teamEventRegistrationService, times(1)).deleteAllRegistrationsOfTeam(userAcc.getTeam());

        verify(teamRepository, times(1)).save(userAcc.getTeam());
        verify(userAccRepository, times(1)).save(userAcc);
    }

    @Test
    void giveLeaderRights() {
        Long userAccIdToGiveLeaderRights = 12L;
        Long initiatorUserAccId = 31L;
        UserAcc userAccToGiveLeaderRights = UserAcc.builder().id(userAccIdToGiveLeaderRights).enabled(true).build();
        UserAcc initiatorUserAcc = UserAcc.builder().id(initiatorUserAccId).enabled(true).build();

        when(userAccRepository.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(userAccIdToGiveLeaderRights)).thenReturn(Optional.of(userAccToGiveLeaderRights));
        when(userAccRepository.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(initiatorUserAccId)).thenReturn(Optional.of(initiatorUserAcc));

        teamLifecycleService.giveLeaderRights(userAccIdToGiveLeaderRights, initiatorUserAccId);

        verify(stateMachineFactory, times(1)).buildTeamLifecycleStateMachine(userAccToGiveLeaderRights, initiatorUserAcc);
        verify(teamLifecycleStateMachine, times(1)).giveLeaderRights();

        verify(userAccRepository, times(1)).save(userAccToGiveLeaderRights);
    }

    @Test
    void takeAwayLeaderRights() {
        Long userAccIdToTakeAwayLeaderRights = 12L;
        Long initiatorUserAccId = 31L;
        UserAcc userAccToTakeAwayLeaderRights = UserAcc.builder().id(userAccIdToTakeAwayLeaderRights).enabled(true).build();
        UserAcc initiatorUserAcc = UserAcc.builder().id(initiatorUserAccId).enabled(true).build();

        when(userAccRepository.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(userAccIdToTakeAwayLeaderRights)).thenReturn(Optional.of(userAccToTakeAwayLeaderRights));
        when(userAccRepository.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(initiatorUserAccId)).thenReturn(Optional.of(initiatorUserAcc));

        teamLifecycleService.takeAwayLeaderRights(userAccIdToTakeAwayLeaderRights, initiatorUserAccId);

        verify(stateMachineFactory, times(1)).buildTeamLifecycleStateMachine(userAccToTakeAwayLeaderRights, initiatorUserAcc);
        verify(teamLifecycleStateMachine, times(1)).takeAwayLeaderRights();

        verify(userAccRepository, times(1)).save(userAccToTakeAwayLeaderRights);
    }

    @Test
    void resignFromLeadership() {
        Long userAccId = 12L;
        UserAcc userAcc = UserAcc.builder().id(userAccId).enabled(true).build();

        when(userAccRepository.findByIdAndEnabledIsTrue_WithPessimisticWriteLock(userAccId)).thenReturn(Optional.of(userAcc));

        teamLifecycleService.resignFromLeadership(userAccId);

        verify(stateMachineFactory, times(1)).buildTeamLifecycleStateMachine(userAcc, userAcc);
        verify(teamLifecycleStateMachine, times(1)).resignFromLeadership();

        verify(userAccRepository, times(1)).save(userAcc);
    }
}