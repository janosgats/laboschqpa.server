package com.laboschqpa.server.statemachine;

import com.laboschqpa.server.api.dto.submission.CreateNewSubmissionDto;
import com.laboschqpa.server.api.dto.submission.EditSubmissionDto;
import com.laboschqpa.server.entity.Team;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.usergeneratedcontent.Objective;
import com.laboschqpa.server.entity.usergeneratedcontent.Submission;
import com.laboschqpa.server.enums.auth.TeamRole;
import com.laboschqpa.server.enums.errorkey.SubmissionApiError;
import com.laboschqpa.server.exceptions.statemachine.SubmissionException;
import com.laboschqpa.server.repo.ObjectiveRepository;
import com.laboschqpa.server.repo.SubmissionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubmissionStateMachineTest {
    @Mock
    ObjectiveRepository objectiveRepository;
    @Mock
    SubmissionRepository submissionRepository;

    @InjectMocks
    StateMachineFactory stateMachineFactory;

    private void assertThrowsSubmissionExceptionWithSpecificError(SubmissionApiError expectedTeamUserRelationApiError, Executable executable) {
        assertThrowsSubmissionExceptionWithSpecificError(false, expectedTeamUserRelationApiError, executable);
    }

    private void assertThrowsSubmissionExceptionWithSpecificError(boolean unwrapInvocationTargetException,
                                                                  SubmissionApiError expectedTeamUserRelationApiError, Executable executable) {
        try {
            try {
                executable.execute();
            } catch (InvocationTargetException e) {
                if (unwrapInvocationTargetException && e.getCause() instanceof SubmissionException)
                    throw e.getCause();
                else
                    throw e;
            }
        } catch (SubmissionException e) {
            if (e.getSubmissionApiError().equals(expectedTeamUserRelationApiError)) {
                return;
            } else {
                throw new RuntimeException("SubmissionException::submissionApiError differs. Expected: " + expectedTeamUserRelationApiError + " Actual: " + e.getSubmissionApiError());
            }
        } catch (Throwable e) {
            throw new RuntimeException("SubmissionException wanted but " + e.getClass() + " was thrown.", e);
        }

        throw new RuntimeException("SubmissionException wanted but no exception was thrown.");
    }

    @Test
    void createNewSubmission() {
        Team team = new Team();
        team.setId(5L);
        UserAcc userAcc = UserAcc.builder().id(10L).team(team).build();

        Objective objective = new Objective();
        objective.setId(20L);

        CreateNewSubmissionDto createNewSubmissionDto = new CreateNewSubmissionDto();
        createNewSubmissionDto.setObjectiveId(objective.getId());
        createNewSubmissionDto.setContent("test content");

        SubmissionStateMachine submissionStateMachine = spy(stateMachineFactory.buildSubmissionStateMachine(userAcc));
        doNothing().when(submissionStateMachine).assertInitiatorUserIsMemberOrLeaderOfItsTeam();
        doReturn(objective).when(submissionStateMachine).findByIdAndAssertObjective_IsSubmittable_DeadlineIsNotPassed(objective.getId());

        submissionStateMachine.createNewSubmission(createNewSubmissionDto);

        verify(submissionRepository, times(1)).save(argThat((a) -> {
            assertEquals(userAcc, a.getCreatorUser());
            assertEquals(userAcc, a.getEditorUser());
            assertEquals(team, a.getTeam());
            assertEquals(objective, a.getObjective());
            assertEquals(createNewSubmissionDto.getContent(), a.getContent());
            return true;
        }));
    }

    @Test
    void editSubmission() {
        UserAcc userAcc = UserAcc.builder().id(10L).build();

        assertThrowsSubmissionExceptionWithSpecificError(SubmissionApiError.SUBMISSION_IS_NOT_FOUND, () -> {
            Long submissionId = 88L;
            when(submissionRepository.findById(submissionId)).thenReturn(Optional.empty());
            SubmissionStateMachine submissionStateMachine = spy(stateMachineFactory.buildSubmissionStateMachine(userAcc));
            submissionStateMachine.editSubmission(EditSubmissionDto.builder().id(submissionId).build());
            verify(submissionStateMachine, times(1)).assertInitiatorUserIsMemberOrLeaderOfItsTeam();
        });

        Submission submission = new Submission();
        submission.setId(99L);

        EditSubmissionDto editSubmissionDto = new EditSubmissionDto();
        editSubmissionDto.setId(submission.getId());
        editSubmissionDto.setContent("test content X");

        SubmissionStateMachine submissionStateMachine = spy(stateMachineFactory.buildSubmissionStateMachine(userAcc));
        when(submissionRepository.findById(editSubmissionDto.getId())).thenReturn(Optional.of(submission));
        doNothing().when(submissionStateMachine).assertIfInitiatorCanModifySubmission(submission);

        submissionStateMachine.editSubmission(editSubmissionDto);

        verify(submissionRepository, times(1)).save(submission);
        verify(submissionStateMachine, times(1)).assertInitiatorUserIsMemberOrLeaderOfItsTeam();
        assertNull(submission.getCreatorUser());
        assertEquals(userAcc, submission.getEditorUser());
        assertEquals(editSubmissionDto.getContent(), submission.getContent());
    }

    @Test
    void deleteSubmission() {
        UserAcc userAcc = UserAcc.builder().id(10L).build();

        assertThrowsSubmissionExceptionWithSpecificError(SubmissionApiError.SUBMISSION_IS_NOT_FOUND, () -> {
            Long submissionIdToDelete = 99L;
            when(submissionRepository.findById(submissionIdToDelete)).thenReturn(Optional.empty());
            SubmissionStateMachine submissionStateMachine = spy(stateMachineFactory.buildSubmissionStateMachine(userAcc));
            submissionStateMachine.deleteSubmission(submissionIdToDelete);
            verify(submissionStateMachine, times(1)).assertInitiatorUserIsMemberOrLeaderOfItsTeam();
        });

        Long submissionIdToDelete = 99L;

        Submission submission = new Submission();
        submission.setId(99L);

        SubmissionStateMachine submissionStateMachine = spy(stateMachineFactory.buildSubmissionStateMachine(userAcc));
        when(submissionRepository.findById(submissionIdToDelete)).thenReturn(Optional.of(submission));
        doNothing().when(submissionStateMachine).assertIfInitiatorCanModifySubmission(submission);

        submissionStateMachine.deleteSubmission(submissionIdToDelete);

        verify(submissionRepository, times(1)).deleteById(submissionIdToDelete);
        verify(submissionStateMachine, times(1)).assertInitiatorUserIsMemberOrLeaderOfItsTeam();
    }

    @Test
    void assertInitiatorUserIsMemberOrLeaderOfItsTeam() {
        assertThrowsSubmissionExceptionWithSpecificError(SubmissionApiError.INITIATOR_IS_NOT_IN_A_TEAM, () -> {
            UserAcc userAcc = UserAcc.builder().team(null).teamRole(TeamRole.NOTHING).build();

            SubmissionStateMachine submissionStateMachine = stateMachineFactory.buildSubmissionStateMachine(userAcc);
            submissionStateMachine.assertInitiatorUserIsMemberOrLeaderOfItsTeam();
        });

        assertThrowsSubmissionExceptionWithSpecificError(SubmissionApiError.INITIATOR_IS_NOT_MEMBER_OR_LEADER_OF_THE_TEAM, () -> {
            UserAcc userAcc = UserAcc.builder().team(new Team()).teamRole(TeamRole.APPLIED).build();

            SubmissionStateMachine submissionStateMachine = stateMachineFactory.buildSubmissionStateMachine(userAcc);
            submissionStateMachine.assertInitiatorUserIsMemberOrLeaderOfItsTeam();
        });

        {
            UserAcc userAcc = UserAcc.builder().team(new Team()).teamRole(TeamRole.MEMBER).build();

            SubmissionStateMachine submissionStateMachine = stateMachineFactory.buildSubmissionStateMachine(userAcc);
            submissionStateMachine.assertInitiatorUserIsMemberOrLeaderOfItsTeam();
        }
        {
            UserAcc userAcc = UserAcc.builder().team(new Team()).teamRole(TeamRole.LEADER).build();

            SubmissionStateMachine submissionStateMachine = stateMachineFactory.buildSubmissionStateMachine(userAcc);
            submissionStateMachine.assertInitiatorUserIsMemberOrLeaderOfItsTeam();
        }
    }

    @Test
    public void assertObjective_IsSubmittable_DeadlineIsNotPassed() {
        assertThrowsSubmissionExceptionWithSpecificError(SubmissionApiError.OBJECTIVE_IS_NOT_SUBMITTABLE, () -> {
            Objective objective = new Objective();
            objective.setSubmittable(false);
            objective.setDeadline(Instant.now().plusSeconds(999999999));

            SubmissionStateMachine submissionStateMachine = stateMachineFactory.buildSubmissionStateMachine(new UserAcc());
            submissionStateMachine.assertObjective_IsSubmittable_DeadlineIsNotPassed(objective);
        });

        assertThrowsSubmissionExceptionWithSpecificError(SubmissionApiError.OBJECTIVE_DEADLINE_HAS_PASSED, () -> {
            Objective objective = new Objective();
            objective.setSubmittable(true);
            objective.setDeadline(Instant.now().minusSeconds(100));

            SubmissionStateMachine submissionStateMachine = stateMachineFactory.buildSubmissionStateMachine(new UserAcc());
            submissionStateMachine.assertObjective_IsSubmittable_DeadlineIsNotPassed(objective);
        });

        Objective objective = new Objective();
        objective.setSubmittable(true);
        objective.setDeadline(Instant.now().plusSeconds(999999999));

        SubmissionStateMachine submissionStateMachine = stateMachineFactory.buildSubmissionStateMachine(new UserAcc());
        submissionStateMachine.assertObjective_IsSubmittable_DeadlineIsNotPassed(objective);
    }

    @Test
    public void findByIdAndAssertObjective_IsSubmittable_DeadlineIsNotPassed() {
        assertThrowsSubmissionExceptionWithSpecificError(SubmissionApiError.OBJECTIVE_IS_NOT_FOUND, () -> {
            Long objectiveId = 11L;
            when(objectiveRepository.findById(objectiveId)).thenReturn(Optional.empty());

            SubmissionStateMachine submissionStateMachine = stateMachineFactory.buildSubmissionStateMachine(new UserAcc());
            submissionStateMachine.findByIdAndAssertObjective_IsSubmittable_DeadlineIsNotPassed(objectiveId);
        });

        Long objectiveId = 11L;
        Objective objective = new Objective();
        when(objectiveRepository.findById(objectiveId)).thenReturn(Optional.of(objective));

        SubmissionStateMachine submissionStateMachine = spy(stateMachineFactory.buildSubmissionStateMachine(new UserAcc()));
        doNothing().when(submissionStateMachine).assertObjective_IsSubmittable_DeadlineIsNotPassed(objective);

        submissionStateMachine.findByIdAndAssertObjective_IsSubmittable_DeadlineIsNotPassed(objectiveId);
        verify(submissionStateMachine, times(1)).assertObjective_IsSubmittable_DeadlineIsNotPassed(objective);
    }

    @Test
    public void assertIfInitiatorIsPermittedToModifySubmission() throws
            NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Team team1 = new Team();
        team1.setId(1L);
        Team team2 = new Team();
        team2.setId(2L);

        assertThrowsSubmissionExceptionWithSpecificError(SubmissionApiError.YOU_HAVE_TO_BE_TEAM_LEADER_TO_MODIFY_THE_SUBMISSION_OF_SOMEONE_ELSE, () -> {
            UserAcc userAcc = UserAcc.builder().id(1L).team(team1).teamRole(TeamRole.MEMBER).build();
            Submission submission = new Submission();
            submission.setCreatorUser(UserAcc.builder().id(2L).build());
            submission.setTeam(team1);

            SubmissionStateMachine submissionStateMachine = spy(stateMachineFactory.buildSubmissionStateMachine(userAcc));
            submissionStateMachine.assertIfInitiatorCanModifySubmission(submission);
        });

        assertThrowsSubmissionExceptionWithSpecificError(SubmissionApiError.YOU_CANNOT_MODIFY_A_SUBMISSION_IF_YOU_ARE_NOT_IN_THE_SUBMITTER_TEAM, () -> {
            UserAcc userAcc = UserAcc.builder().id(1L).team(team1).teamRole(TeamRole.APPLIED).build();
            Submission submission = new Submission();
            submission.setCreatorUser(userAcc);
            submission.setTeam(team1);

            SubmissionStateMachine submissionStateMachine = spy(stateMachineFactory.buildSubmissionStateMachine(userAcc));
            submissionStateMachine.assertIfInitiatorCanModifySubmission(submission);
        });

        assertThrowsSubmissionExceptionWithSpecificError(SubmissionApiError.YOU_CANNOT_MODIFY_A_SUBMISSION_IF_YOU_ARE_NOT_IN_THE_SUBMITTER_TEAM, () -> {
            UserAcc userAcc = UserAcc.builder().id(1L).team(team2).teamRole(TeamRole.LEADER).build();
            Submission submission = new Submission();
            submission.setCreatorUser(userAcc);
            submission.setTeam(team1);

            SubmissionStateMachine submissionStateMachine = spy(stateMachineFactory.buildSubmissionStateMachine(userAcc));
            submissionStateMachine.assertIfInitiatorCanModifySubmission(submission);
        });

        {
            UserAcc userAcc = UserAcc.builder().id(1L).team(team1).teamRole(TeamRole.MEMBER).build();

            Objective objective = new Objective();
            Submission submission = new Submission();
            submission.setCreatorUser(userAcc);
            submission.setObjective(objective);
            submission.setTeam(team1);

            SubmissionStateMachine submissionStateMachine = spy(stateMachineFactory.buildSubmissionStateMachine(userAcc));

            doNothing().when(submissionStateMachine).assertObjective_IsSubmittable_DeadlineIsNotPassed(objective);
            submissionStateMachine.assertIfInitiatorCanModifySubmission(submission);
            verify(submissionStateMachine, times(1)).assertObjective_IsSubmittable_DeadlineIsNotPassed(objective);
        }

        {
            UserAcc userAcc = UserAcc.builder().id(1L).team(team1).teamRole(TeamRole.LEADER).build();

            Objective objective = new Objective();
            Submission submission = new Submission();
            submission.setCreatorUser(UserAcc.builder().id(2L).build());
            submission.setObjective(objective);
            submission.setTeam(team1);

            SubmissionStateMachine submissionStateMachine = spy(stateMachineFactory.buildSubmissionStateMachine(userAcc));

            doNothing().when(submissionStateMachine).assertObjective_IsSubmittable_DeadlineIsNotPassed(objective);
            submissionStateMachine.assertIfInitiatorCanModifySubmission(submission);
            verify(submissionStateMachine, times(1)).assertObjective_IsSubmittable_DeadlineIsNotPassed(objective);
        }
    }
}