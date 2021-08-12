package com.laboschqpa.server.statemachine;

import com.laboschqpa.server.api.dto.team.CreateNewTeamRequest;
import com.laboschqpa.server.entity.Team;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.enums.TeamRole;
import com.laboschqpa.server.enums.apierrordescriptor.TeamLifecycleApiError;
import com.laboschqpa.server.exceptions.apierrordescriptor.TeamUserRelationException;
import com.laboschqpa.server.repo.TeamRepository;
import com.laboschqpa.server.repo.UserAccRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamLifecycleStateMachineTest {
    @Mock
    UserAccRepository userAccRepositoryMock;
    @Mock
    TeamRepository teamRepositoryMock;

    @InjectMocks
    StateMachineFactory stateMachineFactory;

    private void assertThrowsTeamUserRelationExceptionWithSpecificError(TeamLifecycleApiError expectedTeamLifecycleApiError, Executable executable) {
        try {
            executable.execute();
        } catch (TeamUserRelationException e) {
            if (e.getApiErrorDescriptor().equals(expectedTeamLifecycleApiError)) {
                return;
            } else {
                throw new RuntimeException("TeamUserRelationException::teamUserRelationError differs. Expected: " + expectedTeamLifecycleApiError + " Actual: " + e.getApiErrorDescriptor());
            }
        } catch (Throwable e) {
            throw new RuntimeException("TeamUserRelationException wanted but " + e.getClass() + " was thrown.", e);
        }

        throw new RuntimeException("TeamUserRelationException wanted but no exception was thrown.");
    }

    @Test
    void assertInitiatorIsSameAsAltered() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        assertThrowsTeamUserRelationExceptionWithSpecificError(TeamLifecycleApiError.INITIATOR_IS_DIFFERENT_THAN_ALTERED, () ->
                stateMachineFactory.buildTeamLifecycleStateMachine(
                        UserAcc.builder().id(1L).build(),
                        UserAcc.builder().id(2L).build()
                ).assertInitiatorIsSameAsAltered()
        );

        stateMachineFactory.buildTeamLifecycleStateMachine(
                UserAcc.builder().id(1L).build(),
                UserAcc.builder().id(1L).build()
        ).assertInitiatorIsSameAsAltered();
    }

    @Test
    void assertInitiatorIsDifferentThanAltered_and_initiatorIsLeaderOfTeamOfTheAltered() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Team team = new Team(10L, "testTeam", false, false, true);
        Team team2 = new Team(12L, "testTeam2", false, false, true);

        assertThrowsTeamUserRelationExceptionWithSpecificError(TeamLifecycleApiError.INITIATOR_IS_SAME_AS_ALTERED, () ->
                stateMachineFactory.buildTeamLifecycleStateMachine(
                        UserAcc.builder().id(1L).build(),
                        UserAcc.builder().id(1L).build()
                ).assertInitiatorIsDifferentThanAltered_and_initiatorIsLeaderOfTeamOfTheAltered()
        );

        assertThrowsTeamUserRelationExceptionWithSpecificError(TeamLifecycleApiError.INITIATOR_IS_NOT_LEADER_OF_TEAM_OF_ALTERED, () ->
                stateMachineFactory.buildTeamLifecycleStateMachine(
                        UserAcc.builder().id(1L).team(team).teamRole(TeamRole.MEMBER).build(),
                        UserAcc.builder().id(2L).team(team).teamRole(TeamRole.MEMBER).build()
                ).assertInitiatorIsDifferentThanAltered_and_initiatorIsLeaderOfTeamOfTheAltered()
        );

        assertThrowsTeamUserRelationExceptionWithSpecificError(TeamLifecycleApiError.INITIATOR_IS_NOT_LEADER_OF_TEAM_OF_ALTERED, () ->
                stateMachineFactory.buildTeamLifecycleStateMachine(
                        UserAcc.builder().id(1L).team(team).teamRole(TeamRole.MEMBER).build(),
                        UserAcc.builder().id(2L).team(team2).teamRole(TeamRole.LEADER).build()
                ).assertInitiatorIsDifferentThanAltered_and_initiatorIsLeaderOfTeamOfTheAltered()

        );

        stateMachineFactory.buildTeamLifecycleStateMachine(
                UserAcc.builder().id(1L).team(team).teamRole(TeamRole.MEMBER).build(),
                UserAcc.builder().id(2L).team(team).teamRole(TeamRole.LEADER).build()
        ).assertInitiatorIsDifferentThanAltered_and_initiatorIsLeaderOfTeamOfTheAltered();

    }

    @Test
    void createNewTeam() {
        CreateNewTeamRequest createNewTeamRequest = new CreateNewTeamRequest("test name");
        Team team = new Team(10L, createNewTeamRequest.getName(), false, false, true);

        assertThrowsTeamUserRelationExceptionWithSpecificError(TeamLifecycleApiError.YOU_ARE_ALREADY_MEMBER_OR_APPLICANT_OF_A_TEAM, () ->
                stateMachineFactory.buildTeamLifecycleStateMachine(
                        UserAcc.builder().id(1L).team(team).teamRole(TeamRole.MEMBER).build(),
                        UserAcc.builder().id(1L).team(team).teamRole(TeamRole.MEMBER).build()
                ).createNewTeam(createNewTeamRequest)
        );


        TeamLifecycleStateMachine teamLifecycleStateMachine = spy(stateMachineFactory.buildTeamLifecycleStateMachine(
                UserAcc.builder().id(1L).team(null).teamRole(TeamRole.NOTHING).build(),
                UserAcc.builder().id(1L).team(null).teamRole(TeamRole.NOTHING).build()
        ));
        teamLifecycleStateMachine.createNewTeam(createNewTeamRequest);

        assertEquals(team.getName(), teamLifecycleStateMachine.getAlteredUserAcc().getTeam().getName());
        verify(teamLifecycleStateMachine, times(1)).assertInitiatorIsSameAsAltered();
    }

    @Test
    void applyToTeam() {
        Team team = new Team(10L, "test", false, false, true);

        assertThrowsTeamUserRelationExceptionWithSpecificError(TeamLifecycleApiError.YOU_ARE_ALREADY_MEMBER_OR_APPLICANT_OF_A_TEAM, () ->
                stateMachineFactory.buildTeamLifecycleStateMachine(
                        UserAcc.builder().id(1L).team(team).teamRole(TeamRole.MEMBER).build(),
                        UserAcc.builder().id(1L).team(team).teamRole(TeamRole.MEMBER).build()
                ).applyToTeam(team)
        );


        TeamLifecycleStateMachine teamLifecycleStateMachine = spy(stateMachineFactory.buildTeamLifecycleStateMachine(
                UserAcc.builder().id(1L).team(null).teamRole(TeamRole.NOTHING).build(),
                UserAcc.builder().id(1L).team(null).teamRole(TeamRole.NOTHING).build()
        ));
        teamLifecycleStateMachine.applyToTeam(team);

        assertEquals(team.getId(), teamLifecycleStateMachine.getAlteredUserAcc().getTeam().getId());
        assertEquals(TeamRole.APPLICANT, teamLifecycleStateMachine.getAlteredUserAcc().getTeamRole());
        verify(teamLifecycleStateMachine, times(1)).assertInitiatorIsSameAsAltered();
    }

    @Test
    void cancelApplicationToTeam() {
        Team team = new Team(10L, "test", false, false, true);

        assertThrowsTeamUserRelationExceptionWithSpecificError(TeamLifecycleApiError.OPERATION_IS_INVALID_FOR_TEAM_ROLE_OF_ALTERED, () ->
                stateMachineFactory.buildTeamLifecycleStateMachine(
                        UserAcc.builder().id(1L).teamRole(TeamRole.MEMBER).build(),
                        UserAcc.builder().id(1L).teamRole(TeamRole.MEMBER).build()
                ).cancelApplicationToTeam()
        );


        TeamLifecycleStateMachine teamLifecycleStateMachine = spy(stateMachineFactory.buildTeamLifecycleStateMachine(
                UserAcc.builder().id(1L).team(team).teamRole(TeamRole.APPLICANT).build(),
                UserAcc.builder().id(1L).team(team).teamRole(TeamRole.APPLICANT).build()
        ));
        teamLifecycleStateMachine.cancelApplicationToTeam();

        assertNull(teamLifecycleStateMachine.getAlteredUserAcc().getTeam());
        assertEquals(TeamRole.NOTHING, teamLifecycleStateMachine.getAlteredUserAcc().getTeamRole());
        verify(teamLifecycleStateMachine, times(1)).assertInitiatorIsSameAsAltered();
    }

    @Test
    void declineApplicationToTeam() {
        Team team = new Team(10L, "test", false, false, true);

        assertThrowsTeamUserRelationExceptionWithSpecificError(TeamLifecycleApiError.OPERATION_IS_INVALID_FOR_TEAM_ROLE_OF_ALTERED, () ->
                stateMachineFactory.buildTeamLifecycleStateMachine(
                        UserAcc.builder().id(1L).team(team).teamRole(TeamRole.NOTHING).build(),
                        UserAcc.builder().id(2L).team(team).teamRole(TeamRole.LEADER).build()
                ).declineApplicationToTeam()
        );


        TeamLifecycleStateMachine teamLifecycleStateMachine = spy(stateMachineFactory.buildTeamLifecycleStateMachine(
                UserAcc.builder().id(1L).team(team).teamRole(TeamRole.APPLICANT).build(),
                UserAcc.builder().id(2L).team(team).teamRole(TeamRole.LEADER).build()
        ));
        teamLifecycleStateMachine.declineApplicationToTeam();

        assertNull(teamLifecycleStateMachine.getAlteredUserAcc().getTeam());
        assertEquals(TeamRole.NOTHING, teamLifecycleStateMachine.getAlteredUserAcc().getTeamRole());
        verify(teamLifecycleStateMachine, times(1)).assertInitiatorIsDifferentThanAltered_and_initiatorIsLeaderOfTeamOfTheAltered();
    }

    @Test
    void approveApplication() {
        Team team = new Team(10L, "test", false, false, true);

        assertThrowsTeamUserRelationExceptionWithSpecificError(TeamLifecycleApiError.OPERATION_IS_INVALID_FOR_TEAM_ROLE_OF_ALTERED, () ->
                stateMachineFactory.buildTeamLifecycleStateMachine(
                        UserAcc.builder().id(1L).team(team).teamRole(TeamRole.NOTHING).build(),
                        UserAcc.builder().id(2L).team(team).teamRole(TeamRole.LEADER).build()
                ).approveApplication()
        );


        TeamLifecycleStateMachine teamLifecycleStateMachine = spy(stateMachineFactory.buildTeamLifecycleStateMachine(
                UserAcc.builder().id(1L).team(team).teamRole(TeamRole.APPLICANT).build(),
                UserAcc.builder().id(2L).team(team).teamRole(TeamRole.LEADER).build()
        ));
        teamLifecycleStateMachine.approveApplication();

        assertEquals(team.getId(), teamLifecycleStateMachine.getAlteredUserAcc().getTeam().getId());
        assertEquals(TeamRole.MEMBER, teamLifecycleStateMachine.getAlteredUserAcc().getTeamRole());
        verify(teamLifecycleStateMachine, times(1)).assertInitiatorIsDifferentThanAltered_and_initiatorIsLeaderOfTeamOfTheAltered();
    }

    @Test
    void leaveTeam_asMember() {
        Team team = new Team(10L, "test", false, false, true);

        assertThrowsTeamUserRelationExceptionWithSpecificError(TeamLifecycleApiError.OPERATION_IS_INVALID_FOR_TEAM_ROLE_OF_ALTERED, () ->
                stateMachineFactory.buildTeamLifecycleStateMachine(
                        UserAcc.builder().id(1L).teamRole(TeamRole.APPLICANT).build(),
                        UserAcc.builder().id(1L).teamRole(TeamRole.APPLICANT).build()
                ).leaveTeam()
        );


        TeamLifecycleStateMachine teamLifecycleStateMachine = spy(stateMachineFactory.buildTeamLifecycleStateMachine(
                UserAcc.builder().id(1L).team(team).teamRole(TeamRole.MEMBER).build(),
                UserAcc.builder().id(1L).team(team).teamRole(TeamRole.MEMBER).build()
        ));
        teamLifecycleStateMachine.leaveTeam();

        assertNull(teamLifecycleStateMachine.getAlteredUserAcc().getTeam());
        assertEquals(TeamRole.NOTHING, teamLifecycleStateMachine.getAlteredUserAcc().getTeamRole());
        verify(teamLifecycleStateMachine, times(1)).assertInitiatorIsSameAsAltered();
    }

    @Test
    void leaveTeam_asLeader_fails() {
        Team team = new Team(10L, "test", false, false, true);

        assertThrowsTeamUserRelationExceptionWithSpecificError(TeamLifecycleApiError.OPERATION_IS_INVALID_FOR_TEAM_ROLE_OF_ALTERED, () ->
                stateMachineFactory.buildTeamLifecycleStateMachine(
                        UserAcc.builder().id(1L).teamRole(TeamRole.NOTHING).build(),
                        UserAcc.builder().id(1L).teamRole(TeamRole.NOTHING).build()
                ).leaveTeam()
        );

        when(userAccRepositoryMock.getCountOfEnabledLeadersInTeam(team)).thenReturn(1);

        TeamLifecycleStateMachine teamLifecycleStateMachine = spy(stateMachineFactory.buildTeamLifecycleStateMachine(
                UserAcc.builder().id(1L).team(team).teamRole(TeamRole.LEADER).build(),
                UserAcc.builder().id(1L).team(team).teamRole(TeamRole.LEADER).build()
        ));

        assertThrowsTeamUserRelationExceptionWithSpecificError(TeamLifecycleApiError.THERE_IS_NO_OTHER_LEADER, teamLifecycleStateMachine::leaveTeam);
        verify(teamLifecycleStateMachine, times(1)).assertInitiatorIsSameAsAltered();
    }

    @Test
    void leaveTeam_asLeader_success() {
        Team team = new Team(10L, "test", false, false, true);

        TeamLifecycleStateMachine teamLifecycleStateMachine = spy(stateMachineFactory.buildTeamLifecycleStateMachine(
                UserAcc.builder().id(1L).team(team).teamRole(TeamRole.LEADER).build(),
                UserAcc.builder().id(1L).team(team).teamRole(TeamRole.LEADER).build()
        ));

        when(userAccRepositoryMock.getCountOfEnabledLeadersInTeam(team)).thenReturn(5);
        teamLifecycleStateMachine.leaveTeam();

        assertNull(teamLifecycleStateMachine.getAlteredUserAcc().getTeam());
        assertEquals(TeamRole.NOTHING, teamLifecycleStateMachine.getAlteredUserAcc().getTeamRole());
        verify(teamLifecycleStateMachine, times(1)).assertInitiatorIsSameAsAltered();
    }

    @Test
    void kickFromTeam() {
        Team team = new Team(10L, "test", false, false, true);

        assertThrowsTeamUserRelationExceptionWithSpecificError(TeamLifecycleApiError.OPERATION_IS_INVALID_FOR_TEAM_ROLE_OF_ALTERED, () ->
                stateMachineFactory.buildTeamLifecycleStateMachine(
                        UserAcc.builder().id(1L).team(team).teamRole(TeamRole.APPLICANT).build(),
                        UserAcc.builder().id(2L).team(team).teamRole(TeamRole.LEADER).build()
                ).kickFromTeam()
        );


        TeamLifecycleStateMachine teamLifecycleStateMachine = spy(stateMachineFactory.buildTeamLifecycleStateMachine(
                UserAcc.builder().id(1L).team(team).teamRole(TeamRole.LEADER).build(),
                UserAcc.builder().id(2L).team(team).teamRole(TeamRole.LEADER).build()
        ));
        teamLifecycleStateMachine.kickFromTeam();

        assertNull(teamLifecycleStateMachine.getAlteredUserAcc().getTeam());
        assertEquals(TeamRole.NOTHING, teamLifecycleStateMachine.getAlteredUserAcc().getTeamRole());
        verify(teamLifecycleStateMachine, times(1)).assertInitiatorIsDifferentThanAltered_and_initiatorIsLeaderOfTeamOfTheAltered();
    }

    @Test
    void archiveAndLeaveTeam() {
        Team team = new Team(10L, "test", false, false, true);

        assertThrowsTeamUserRelationExceptionWithSpecificError(TeamLifecycleApiError.YOU_HAVE_TO_BE_A_LEADER_TO_DO_THIS_OPERATION, () ->
                stateMachineFactory.buildTeamLifecycleStateMachine(
                        UserAcc.builder().id(1L).teamRole(TeamRole.MEMBER).build(),
                        UserAcc.builder().id(1L).teamRole(TeamRole.MEMBER).build()
                ).archiveAndLeaveTeam()
        );

        TeamLifecycleStateMachine teamLifecycleStateMachine = spy(stateMachineFactory.buildTeamLifecycleStateMachine(
                UserAcc.builder().id(1L).team(team).teamRole(TeamRole.LEADER).build(),
                UserAcc.builder().id(1L).team(team).teamRole(TeamRole.LEADER).build()
        ));
        teamLifecycleStateMachine.archiveAndLeaveTeam();

        assertTrue(team.getArchived());
        verify(userAccRepositoryMock, times(1)).kickEveryoneFromTeam(team);
        verify(teamLifecycleStateMachine, times(1)).assertInitiatorIsSameAsAltered();
    }

    @Test
    void giveLeaderRights() {
        Team team = new Team(10L, "test", false, false, true);

        assertThrowsTeamUserRelationExceptionWithSpecificError(TeamLifecycleApiError.OPERATION_IS_INVALID_FOR_TEAM_ROLE_OF_ALTERED, () ->
                stateMachineFactory.buildTeamLifecycleStateMachine(
                        UserAcc.builder().id(1L).team(team).teamRole(TeamRole.APPLICANT).build(),
                        UserAcc.builder().id(2L).team(team).teamRole(TeamRole.LEADER).build()
                ).giveLeaderRights()
        );


        TeamLifecycleStateMachine teamLifecycleStateMachine = spy(stateMachineFactory.buildTeamLifecycleStateMachine(
                UserAcc.builder().id(1L).team(team).teamRole(TeamRole.MEMBER).build(),
                UserAcc.builder().id(2L).team(team).teamRole(TeamRole.LEADER).build()
        ));
        teamLifecycleStateMachine.giveLeaderRights();

        assertEquals(TeamRole.LEADER, teamLifecycleStateMachine.getAlteredUserAcc().getTeamRole());
        verify(teamLifecycleStateMachine, times(1)).assertInitiatorIsDifferentThanAltered_and_initiatorIsLeaderOfTeamOfTheAltered();
    }

    @Test
    void takeAwayLeaderRights() {
        Team team = new Team(10L, "test", false, false, true);

        assertThrowsTeamUserRelationExceptionWithSpecificError(TeamLifecycleApiError.OPERATION_IS_INVALID_FOR_TEAM_ROLE_OF_ALTERED, () ->
                stateMachineFactory.buildTeamLifecycleStateMachine(
                        UserAcc.builder().id(1L).team(team).teamRole(TeamRole.APPLICANT).build(),
                        UserAcc.builder().id(2L).team(team).teamRole(TeamRole.LEADER).build()
                ).takeAwayLeaderRights()
        );

        TeamLifecycleStateMachine teamLifecycleStateMachine = spy(stateMachineFactory.buildTeamLifecycleStateMachine(
                UserAcc.builder().id(1L).team(team).teamRole(TeamRole.LEADER).build(),
                UserAcc.builder().id(2L).team(team).teamRole(TeamRole.LEADER).build()
        ));
        teamLifecycleStateMachine.takeAwayLeaderRights();

        assertEquals(TeamRole.MEMBER, teamLifecycleStateMachine.getAlteredUserAcc().getTeamRole());
        verify(teamLifecycleStateMachine, times(1)).assertInitiatorIsDifferentThanAltered_and_initiatorIsLeaderOfTeamOfTheAltered();
    }

    @Test
    void resignFromLeadership_fails() {
        Team team = new Team(10L, "test", false, false, true);

        assertThrowsTeamUserRelationExceptionWithSpecificError(TeamLifecycleApiError.OPERATION_IS_INVALID_FOR_TEAM_ROLE_OF_ALTERED, () ->
                stateMachineFactory.buildTeamLifecycleStateMachine(
                        UserAcc.builder().id(1L).teamRole(TeamRole.MEMBER).build(),
                        UserAcc.builder().id(1L).teamRole(TeamRole.MEMBER).build()
                ).resignFromLeadership()
        );

        TeamLifecycleStateMachine teamLifecycleStateMachine = spy(stateMachineFactory.buildTeamLifecycleStateMachine(
                UserAcc.builder().id(1L).team(team).teamRole(TeamRole.LEADER).build(),
                UserAcc.builder().id(1L).team(team).teamRole(TeamRole.LEADER).build()
        ));
        assertThrowsTeamUserRelationExceptionWithSpecificError(TeamLifecycleApiError.THERE_IS_NO_OTHER_LEADER, teamLifecycleStateMachine::resignFromLeadership);

        verify(teamLifecycleStateMachine, times(1)).assertInitiatorIsSameAsAltered();
    }

    @Test
    void resignFromLeadership_success() {
        Team team = new Team(10L, "test", false, false, true);

        TeamLifecycleStateMachine teamLifecycleStateMachine = spy(stateMachineFactory.buildTeamLifecycleStateMachine(
                UserAcc.builder().id(1L).team(team).teamRole(TeamRole.LEADER).build(),
                UserAcc.builder().id(1L).team(team).teamRole(TeamRole.LEADER).build()
        ));

        when(userAccRepositoryMock.getCountOfEnabledLeadersInTeam(team)).thenReturn(2);
        teamLifecycleStateMachine.resignFromLeadership();

        verify(teamLifecycleStateMachine, times(1)).assertInitiatorIsSameAsAltered();
    }
}