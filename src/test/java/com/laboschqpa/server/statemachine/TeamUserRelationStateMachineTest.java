package com.laboschqpa.server.statemachine;

import com.laboschqpa.server.api.dto.team.TeamDto;
import com.laboschqpa.server.entity.Team;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.enums.auth.TeamRole;
import com.laboschqpa.server.enums.errorkey.TeamUserRelationApiError;
import com.laboschqpa.server.exceptions.statemachine.TeamUserRelationException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamUserRelationStateMachineTest {
    @Mock
    UserAccRepository userAccRepositoryMock;
    @Mock
    TeamRepository teamRepositoryMock;

    @InjectMocks
    StateMachineFactory stateMachineFactory;

    private void assertThrowsTeamUserRelationExceptionWithSpecificError(TeamUserRelationApiError expectedTeamUserRelationApiError, Executable executable) {
        try {
            executable.execute();
        } catch (TeamUserRelationException e) {
            if (e.getTeamUserRelationApiError().equals(expectedTeamUserRelationApiError)) {
                return;
            } else {
                throw new RuntimeException("TeamUserRelationException::teamUserRelationError differs. Expected: " + expectedTeamUserRelationApiError + " Actual: " + e.getTeamUserRelationApiError());
            }
        } catch (Throwable e) {
            throw new RuntimeException("TeamUserRelationException wanted but " + e.getClass() + " was thrown.", e);
        }

        throw new RuntimeException("TeamUserRelationException wanted but no exception was thrown.");
    }

    @Test
    void assertInitiatorIsSameAsAltered() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        assertThrowsTeamUserRelationExceptionWithSpecificError(TeamUserRelationApiError.INITIATOR_IS_DIFFERENT_THAN_ALTERED, () ->
                stateMachineFactory.buildTeamUserRelationStateMachine(
                        UserAcc.builder().id(1L).build(),
                        UserAcc.builder().id(2L).build()
                ).assertInitiatorIsSameAsAltered()
        );

        stateMachineFactory.buildTeamUserRelationStateMachine(
                UserAcc.builder().id(1L).build(),
                UserAcc.builder().id(1L).build()
        ).assertInitiatorIsSameAsAltered();
    }

    @Test
    void assertInitiatorIsDifferentThanAltered_and_initiatorIsLeaderOfTeamOfTheAltered() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Team team = new Team(10L, "testTeam", false);
        Team team2 = new Team(12L, "testTeam2", false);

        assertThrowsTeamUserRelationExceptionWithSpecificError(TeamUserRelationApiError.INITIATOR_IS_SAME_AS_ALTERED, () ->
                stateMachineFactory.buildTeamUserRelationStateMachine(
                        UserAcc.builder().id(1L).build(),
                        UserAcc.builder().id(1L).build()
                ).assertInitiatorIsDifferentThanAltered_and_initiatorIsLeaderOfTeamOfTheAltered()
        );

        assertThrowsTeamUserRelationExceptionWithSpecificError(TeamUserRelationApiError.INITIATOR_IS_NOT_LEADER_OF_TEAM_OF_ALTERED, () ->
                stateMachineFactory.buildTeamUserRelationStateMachine(
                        UserAcc.builder().id(1L).team(team).teamRole(TeamRole.MEMBER).build(),
                        UserAcc.builder().id(2L).team(team).teamRole(TeamRole.MEMBER).build()
                ).assertInitiatorIsDifferentThanAltered_and_initiatorIsLeaderOfTeamOfTheAltered()
        );

        assertThrowsTeamUserRelationExceptionWithSpecificError(TeamUserRelationApiError.INITIATOR_IS_NOT_LEADER_OF_TEAM_OF_ALTERED, () ->
                stateMachineFactory.buildTeamUserRelationStateMachine(
                        UserAcc.builder().id(1L).team(team).teamRole(TeamRole.MEMBER).build(),
                        UserAcc.builder().id(2L).team(team2).teamRole(TeamRole.LEADER).build()
                ).assertInitiatorIsDifferentThanAltered_and_initiatorIsLeaderOfTeamOfTheAltered()

        );

        stateMachineFactory.buildTeamUserRelationStateMachine(
                UserAcc.builder().id(1L).team(team).teamRole(TeamRole.MEMBER).build(),
                UserAcc.builder().id(2L).team(team).teamRole(TeamRole.LEADER).build()
        ).assertInitiatorIsDifferentThanAltered_and_initiatorIsLeaderOfTeamOfTheAltered();

    }

    @Test
    void createNewTeam() {
        TeamDto teamDto = new TeamDto(10L, "test name");
        Team team = new Team(teamDto.getId(), teamDto.getName(), false);

        assertThrowsTeamUserRelationExceptionWithSpecificError(TeamUserRelationApiError.YOU_ARE_ALREADY_MEMBER_OF_A_TEAM, () ->
                stateMachineFactory.buildTeamUserRelationStateMachine(
                        UserAcc.builder().id(1L).team(team).teamRole(TeamRole.MEMBER).build(),
                        UserAcc.builder().id(1L).team(team).teamRole(TeamRole.MEMBER).build()
                ).createNewTeam(teamDto)
        );


        TeamUserRelationStateMachine teamUserRelationStateMachine = spy(stateMachineFactory.buildTeamUserRelationStateMachine(
                UserAcc.builder().id(1L).team(null).teamRole(TeamRole.NOTHING).build(),
                UserAcc.builder().id(1L).team(null).teamRole(TeamRole.NOTHING).build()
        ));
        teamUserRelationStateMachine.createNewTeam(teamDto);

        assertEquals(team.getName(), teamUserRelationStateMachine.getAlteredUserAcc().getTeam().getName());
        verify(teamUserRelationStateMachine, times(1)).assertInitiatorIsSameAsAltered();
    }

    @Test
    void applyToTeam() {
        Team team = new Team(10L, "test", false);

        assertThrowsTeamUserRelationExceptionWithSpecificError(TeamUserRelationApiError.YOU_ARE_ALREADY_MEMBER_OF_A_TEAM, () ->
                stateMachineFactory.buildTeamUserRelationStateMachine(
                        UserAcc.builder().id(1L).team(team).teamRole(TeamRole.MEMBER).build(),
                        UserAcc.builder().id(1L).team(team).teamRole(TeamRole.MEMBER).build()
                ).applyToTeam(team)
        );


        TeamUserRelationStateMachine teamUserRelationStateMachine = spy(stateMachineFactory.buildTeamUserRelationStateMachine(
                UserAcc.builder().id(1L).team(null).teamRole(TeamRole.NOTHING).build(),
                UserAcc.builder().id(1L).team(null).teamRole(TeamRole.NOTHING).build()
        ));
        teamUserRelationStateMachine.applyToTeam(team);

        assertEquals(team.getId(), teamUserRelationStateMachine.getAlteredUserAcc().getTeam().getId());
        assertEquals(TeamRole.APPLIED, teamUserRelationStateMachine.getAlteredUserAcc().getTeamRole());
        verify(teamUserRelationStateMachine, times(1)).assertInitiatorIsSameAsAltered();
    }

    @Test
    void cancelApplicationToTeam() {
        Team team = new Team(10L, "test", false);

        assertThrowsTeamUserRelationExceptionWithSpecificError(TeamUserRelationApiError.OPERATION_IS_INVALID_FOR_TEAM_ROLE_OF_ALTERED, () ->
                stateMachineFactory.buildTeamUserRelationStateMachine(
                        UserAcc.builder().id(1L).teamRole(TeamRole.MEMBER).build(),
                        UserAcc.builder().id(1L).teamRole(TeamRole.MEMBER).build()
                ).cancelApplicationToTeam()
        );


        TeamUserRelationStateMachine teamUserRelationStateMachine = spy(stateMachineFactory.buildTeamUserRelationStateMachine(
                UserAcc.builder().id(1L).team(team).teamRole(TeamRole.APPLIED).build(),
                UserAcc.builder().id(1L).team(team).teamRole(TeamRole.APPLIED).build()
        ));
        teamUserRelationStateMachine.cancelApplicationToTeam();

        assertNull(teamUserRelationStateMachine.getAlteredUserAcc().getTeam());
        assertEquals(TeamRole.NOTHING, teamUserRelationStateMachine.getAlteredUserAcc().getTeamRole());
        verify(teamUserRelationStateMachine, times(1)).assertInitiatorIsSameAsAltered();
    }

    @Test
    void declineApplicationToTeam() {
        Team team = new Team(10L, "test", false);

        assertThrowsTeamUserRelationExceptionWithSpecificError(TeamUserRelationApiError.OPERATION_IS_INVALID_FOR_TEAM_ROLE_OF_ALTERED, () ->
                stateMachineFactory.buildTeamUserRelationStateMachine(
                        UserAcc.builder().id(1L).team(team).teamRole(TeamRole.NOTHING).build(),
                        UserAcc.builder().id(2L).team(team).teamRole(TeamRole.LEADER).build()
                ).declineApplicationToTeam()
        );


        TeamUserRelationStateMachine teamUserRelationStateMachine = spy(stateMachineFactory.buildTeamUserRelationStateMachine(
                UserAcc.builder().id(1L).team(team).teamRole(TeamRole.APPLIED).build(),
                UserAcc.builder().id(2L).team(team).teamRole(TeamRole.LEADER).build()
        ));
        teamUserRelationStateMachine.declineApplicationToTeam();

        assertNull(teamUserRelationStateMachine.getAlteredUserAcc().getTeam());
        assertEquals(TeamRole.NOTHING, teamUserRelationStateMachine.getAlteredUserAcc().getTeamRole());
        verify(teamUserRelationStateMachine, times(1)).assertInitiatorIsDifferentThanAltered_and_initiatorIsLeaderOfTeamOfTheAltered();
    }

    @Test
    void approveApplication() {
        Team team = new Team(10L, "test", false);

        assertThrowsTeamUserRelationExceptionWithSpecificError(TeamUserRelationApiError.OPERATION_IS_INVALID_FOR_TEAM_ROLE_OF_ALTERED, () ->
                stateMachineFactory.buildTeamUserRelationStateMachine(
                        UserAcc.builder().id(1L).team(team).teamRole(TeamRole.NOTHING).build(),
                        UserAcc.builder().id(2L).team(team).teamRole(TeamRole.LEADER).build()
                ).approveApplication()
        );


        TeamUserRelationStateMachine teamUserRelationStateMachine = spy(stateMachineFactory.buildTeamUserRelationStateMachine(
                UserAcc.builder().id(1L).team(team).teamRole(TeamRole.APPLIED).build(),
                UserAcc.builder().id(2L).team(team).teamRole(TeamRole.LEADER).build()
        ));
        teamUserRelationStateMachine.approveApplication();

        assertEquals(team.getId(), teamUserRelationStateMachine.getAlteredUserAcc().getTeam().getId());
        assertEquals(TeamRole.MEMBER, teamUserRelationStateMachine.getAlteredUserAcc().getTeamRole());
        verify(teamUserRelationStateMachine, times(1)).assertInitiatorIsDifferentThanAltered_and_initiatorIsLeaderOfTeamOfTheAltered();
    }

    @Test
    void leaveTeam_asMember() {
        Team team = new Team(10L, "test", false);

        assertThrowsTeamUserRelationExceptionWithSpecificError(TeamUserRelationApiError.OPERATION_IS_INVALID_FOR_TEAM_ROLE_OF_ALTERED, () ->
                stateMachineFactory.buildTeamUserRelationStateMachine(
                        UserAcc.builder().id(1L).teamRole(TeamRole.APPLIED).build(),
                        UserAcc.builder().id(1L).teamRole(TeamRole.APPLIED).build()
                ).leaveTeam()
        );


        TeamUserRelationStateMachine teamUserRelationStateMachine = spy(stateMachineFactory.buildTeamUserRelationStateMachine(
                UserAcc.builder().id(1L).team(team).teamRole(TeamRole.MEMBER).build(),
                UserAcc.builder().id(1L).team(team).teamRole(TeamRole.MEMBER).build()
        ));
        teamUserRelationStateMachine.leaveTeam();

        assertNull(teamUserRelationStateMachine.getAlteredUserAcc().getTeam());
        assertEquals(TeamRole.NOTHING, teamUserRelationStateMachine.getAlteredUserAcc().getTeamRole());
        verify(teamUserRelationStateMachine, times(1)).assertInitiatorIsSameAsAltered();
    }

    @Test
    void leaveTeam_asLeader_fails() {
        Team team = new Team(10L, "test", false);

        assertThrowsTeamUserRelationExceptionWithSpecificError(TeamUserRelationApiError.OPERATION_IS_INVALID_FOR_TEAM_ROLE_OF_ALTERED, () ->
                stateMachineFactory.buildTeamUserRelationStateMachine(
                        UserAcc.builder().id(1L).teamRole(TeamRole.NOTHING).build(),
                        UserAcc.builder().id(1L).teamRole(TeamRole.NOTHING).build()
                ).leaveTeam()
        );

        when(userAccRepositoryMock.getCountOfEnabledLeadersInTeam(team)).thenReturn(1);

        TeamUserRelationStateMachine teamUserRelationStateMachine = spy(stateMachineFactory.buildTeamUserRelationStateMachine(
                UserAcc.builder().id(1L).team(team).teamRole(TeamRole.LEADER).build(),
                UserAcc.builder().id(1L).team(team).teamRole(TeamRole.LEADER).build()
        ));

        assertThrowsTeamUserRelationExceptionWithSpecificError(TeamUserRelationApiError.THERE_IS_NO_OTHER_LEADER, teamUserRelationStateMachine::leaveTeam);
        verify(teamUserRelationStateMachine, times(1)).assertInitiatorIsSameAsAltered();
    }

    @Test
    void leaveTeam_asLeader_success() {
        Team team = new Team(10L, "test", false);

        TeamUserRelationStateMachine teamUserRelationStateMachine = spy(stateMachineFactory.buildTeamUserRelationStateMachine(
                UserAcc.builder().id(1L).team(team).teamRole(TeamRole.LEADER).build(),
                UserAcc.builder().id(1L).team(team).teamRole(TeamRole.LEADER).build()
        ));

        when(userAccRepositoryMock.getCountOfEnabledLeadersInTeam(team)).thenReturn(5);
        teamUserRelationStateMachine.leaveTeam();

        assertNull(teamUserRelationStateMachine.getAlteredUserAcc().getTeam());
        assertEquals(TeamRole.NOTHING, teamUserRelationStateMachine.getAlteredUserAcc().getTeamRole());
        verify(teamUserRelationStateMachine, times(1)).assertInitiatorIsSameAsAltered();
    }

    @Test
    void kickFromTeam() {
        Team team = new Team(10L, "test", false);

        assertThrowsTeamUserRelationExceptionWithSpecificError(TeamUserRelationApiError.OPERATION_IS_INVALID_FOR_TEAM_ROLE_OF_ALTERED, () ->
                stateMachineFactory.buildTeamUserRelationStateMachine(
                        UserAcc.builder().id(1L).team(team).teamRole(TeamRole.APPLIED).build(),
                        UserAcc.builder().id(2L).team(team).teamRole(TeamRole.LEADER).build()
                ).kickFromTeam()
        );


        TeamUserRelationStateMachine teamUserRelationStateMachine = spy(stateMachineFactory.buildTeamUserRelationStateMachine(
                UserAcc.builder().id(1L).team(team).teamRole(TeamRole.LEADER).build(),
                UserAcc.builder().id(2L).team(team).teamRole(TeamRole.LEADER).build()
        ));
        teamUserRelationStateMachine.kickFromTeam();

        assertNull(teamUserRelationStateMachine.getAlteredUserAcc().getTeam());
        assertEquals(TeamRole.NOTHING, teamUserRelationStateMachine.getAlteredUserAcc().getTeamRole());
        verify(teamUserRelationStateMachine, times(1)).assertInitiatorIsDifferentThanAltered_and_initiatorIsLeaderOfTeamOfTheAltered();
    }

    @Test
    void archiveAndLeaveTeam() {
        Team team = new Team(10L, "test", false);

        assertThrowsTeamUserRelationExceptionWithSpecificError(TeamUserRelationApiError.YOU_HAVE_TO_BE_A_LEADER_TO_DO_THIS_OPERATION, () ->
                stateMachineFactory.buildTeamUserRelationStateMachine(
                        UserAcc.builder().id(1L).teamRole(TeamRole.MEMBER).build(),
                        UserAcc.builder().id(1L).teamRole(TeamRole.MEMBER).build()
                ).archiveAndLeaveTeam()
        );

        TeamUserRelationStateMachine teamUserRelationStateMachine = spy(stateMachineFactory.buildTeamUserRelationStateMachine(
                UserAcc.builder().id(1L).team(team).teamRole(TeamRole.LEADER).build(),
                UserAcc.builder().id(1L).team(team).teamRole(TeamRole.LEADER).build()
        ));
        teamUserRelationStateMachine.archiveAndLeaveTeam();

        assertTrue(team.getArchived());
        verify(userAccRepositoryMock, times(1)).kickEveryoneFromTeam(team);
        verify(teamUserRelationStateMachine, times(1)).assertInitiatorIsSameAsAltered();
    }

    @Test
    void giveLeaderRights() {
        Team team = new Team(10L, "test", false);

        assertThrowsTeamUserRelationExceptionWithSpecificError(TeamUserRelationApiError.OPERATION_IS_INVALID_FOR_TEAM_ROLE_OF_ALTERED, () ->
                stateMachineFactory.buildTeamUserRelationStateMachine(
                        UserAcc.builder().id(1L).team(team).teamRole(TeamRole.APPLIED).build(),
                        UserAcc.builder().id(2L).team(team).teamRole(TeamRole.LEADER).build()
                ).giveLeaderRights()
        );


        TeamUserRelationStateMachine teamUserRelationStateMachine = spy(stateMachineFactory.buildTeamUserRelationStateMachine(
                UserAcc.builder().id(1L).team(team).teamRole(TeamRole.MEMBER).build(),
                UserAcc.builder().id(2L).team(team).teamRole(TeamRole.LEADER).build()
        ));
        teamUserRelationStateMachine.giveLeaderRights();

        assertEquals(TeamRole.LEADER, teamUserRelationStateMachine.getAlteredUserAcc().getTeamRole());
        verify(teamUserRelationStateMachine, times(1)).assertInitiatorIsDifferentThanAltered_and_initiatorIsLeaderOfTeamOfTheAltered();
    }

    @Test
    void takeAwayLeaderRights() {
        Team team = new Team(10L, "test", false);

        assertThrowsTeamUserRelationExceptionWithSpecificError(TeamUserRelationApiError.OPERATION_IS_INVALID_FOR_TEAM_ROLE_OF_ALTERED, () ->
                stateMachineFactory.buildTeamUserRelationStateMachine(
                        UserAcc.builder().id(1L).team(team).teamRole(TeamRole.APPLIED).build(),
                        UserAcc.builder().id(2L).team(team).teamRole(TeamRole.LEADER).build()
                ).takeAwayLeaderRights()
        );

        TeamUserRelationStateMachine teamUserRelationStateMachine = spy(stateMachineFactory.buildTeamUserRelationStateMachine(
                UserAcc.builder().id(1L).team(team).teamRole(TeamRole.LEADER).build(),
                UserAcc.builder().id(2L).team(team).teamRole(TeamRole.LEADER).build()
        ));
        teamUserRelationStateMachine.takeAwayLeaderRights();

        assertEquals(TeamRole.MEMBER, teamUserRelationStateMachine.getAlteredUserAcc().getTeamRole());
        verify(teamUserRelationStateMachine, times(1)).assertInitiatorIsDifferentThanAltered_and_initiatorIsLeaderOfTeamOfTheAltered();
    }

    @Test
    void resignFromLeadership_fails() {
        Team team = new Team(10L, "test", false);

        assertThrowsTeamUserRelationExceptionWithSpecificError(TeamUserRelationApiError.OPERATION_IS_INVALID_FOR_TEAM_ROLE_OF_ALTERED, () ->
                stateMachineFactory.buildTeamUserRelationStateMachine(
                        UserAcc.builder().id(1L).teamRole(TeamRole.MEMBER).build(),
                        UserAcc.builder().id(1L).teamRole(TeamRole.MEMBER).build()
                ).resignFromLeadership()
        );

        TeamUserRelationStateMachine teamUserRelationStateMachine = spy(stateMachineFactory.buildTeamUserRelationStateMachine(
                UserAcc.builder().id(1L).team(team).teamRole(TeamRole.LEADER).build(),
                UserAcc.builder().id(1L).team(team).teamRole(TeamRole.LEADER).build()
        ));
        assertThrowsTeamUserRelationExceptionWithSpecificError(TeamUserRelationApiError.THERE_IS_NO_OTHER_LEADER, teamUserRelationStateMachine::resignFromLeadership);

        verify(teamUserRelationStateMachine, times(1)).assertInitiatorIsSameAsAltered();
    }

    @Test
    void resignFromLeadership_success() {
        Team team = new Team(10L, "test", false);

        TeamUserRelationStateMachine teamUserRelationStateMachine = spy(stateMachineFactory.buildTeamUserRelationStateMachine(
                UserAcc.builder().id(1L).team(team).teamRole(TeamRole.LEADER).build(),
                UserAcc.builder().id(1L).team(team).teamRole(TeamRole.LEADER).build()
        ));

        when(userAccRepositoryMock.getCountOfEnabledLeadersInTeam(team)).thenReturn(2);
        teamUserRelationStateMachine.resignFromLeadership();

        verify(teamUserRelationStateMachine, times(1)).assertInitiatorIsSameAsAltered();
    }
}