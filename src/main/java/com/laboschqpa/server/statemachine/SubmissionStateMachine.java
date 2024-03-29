package com.laboschqpa.server.statemachine;

import com.laboschqpa.server.api.dto.ugc.submission.CreateNewSubmissionDto;
import com.laboschqpa.server.api.dto.ugc.submission.EditSubmissionRequest;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.usergeneratedcontent.Objective;
import com.laboschqpa.server.entity.usergeneratedcontent.Submission;
import com.laboschqpa.server.enums.TeamRole;
import com.laboschqpa.server.enums.apierrordescriptor.SubmissionApiError;
import com.laboschqpa.server.exceptions.apierrordescriptor.SubmissionException;
import com.laboschqpa.server.repo.usergeneratedcontent.ObjectiveRepository;
import com.laboschqpa.server.repo.usergeneratedcontent.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class SubmissionStateMachine {
    private final UserAcc initiatorUserAcc;
    private final ObjectiveRepository objectiveRepository;
    private final SubmissionRepository submissionRepository;

    public Submission createNewSubmission(CreateNewSubmissionDto createNewSubmissionDto) {
        assertInitiatorUserIsMemberOrLeaderOfItsTeam();

        Objective objective = findByIdAndAssertObjective_IsSubmittable_DeadlineIsNotPassed(createNewSubmissionDto.getObjectiveId());

        Submission newSubmission = new Submission();
        newSubmission.setUGCAsCreatedByUser(initiatorUserAcc);
        newSubmission.setAttachments(createNewSubmissionDto.getAttachments());

        newSubmission.setTeam(initiatorUserAcc.getTeam());
        newSubmission.setObjective(objective);
        newSubmission.setContent(createNewSubmissionDto.getContent());

        submissionRepository.save(newSubmission);
        log.debug("UserAcc {} created new submission {}.", initiatorUserAcc.getId(), newSubmission.getId());
        return newSubmission;
    }

    /**
     * Objective and Team is NOT modifiable.
     */
    public void editSubmission(EditSubmissionRequest editSubmissionRequest) {
        Optional<Submission> submissionOptional = submissionRepository.findById(editSubmissionRequest.getId());
        if (submissionOptional.isEmpty()) {
            throw new SubmissionException(SubmissionApiError.SUBMISSION_IS_NOT_FOUND);
        }

        Submission submission = submissionOptional.get();
        assertIfInitiatorCanModifySubmission(submission);

        submission.setUGCAsEditedByUser(initiatorUserAcc);
        submission.setAttachments(editSubmissionRequest.getAttachments());

        submission.setContent(editSubmissionRequest.getContent());

        log.debug("UserAcc {} edited submission {}.", initiatorUserAcc.getId(), submission.getId());
        submissionRepository.save(submission);
    }

    public void deleteSubmission(Long submissionIdToDelete) {
        Optional<Submission> submissionOptional = submissionRepository.findById(submissionIdToDelete);
        if (submissionOptional.isEmpty()) {
            throw new SubmissionException(SubmissionApiError.SUBMISSION_IS_NOT_FOUND);
        }

        Submission submission = submissionOptional.get();
        assertIfInitiatorCanModifySubmission(submission);

        submissionRepository.deleteById(submission.getId());
        log.debug("UserAcc {} deleted submission {}.", initiatorUserAcc.getId(), submission.getId());
    }

    void assertIfInitiatorCanModifySubmission(Submission submission) {
        if (!initiatorUserAcc.getTeamRole().isMemberOrLeader()
                || !(
                submission.getTeam().getId().equals(initiatorUserAcc.getTeam().getId())
                        && initiatorUserAcc.getTeamRole().isMemberOrLeader()
        )) {
            throw new SubmissionException(SubmissionApiError.YOU_CANNOT_MODIFY_A_SUBMISSION_IF_YOU_ARE_NOT_IN_THE_SUBMITTER_TEAM);
        }

        if (!initiatorUserAcc.getId().equals(submission.getCreatorUser().getId())) {
            if (!(
                    submission.getTeam().getId().equals(initiatorUserAcc.getTeam().getId())
                            && initiatorUserAcc.getTeamRole() == TeamRole.LEADER)) {
                throw new SubmissionException(SubmissionApiError.YOU_HAVE_TO_BE_TEAM_LEADER_TO_MODIFY_THE_SUBMISSION_OF_SOMEONE_ELSE);
            }
        }

        assertObjective_IsSubmittable_DeadlineIsNotPassed(submission.getObjective());
    }

    Objective findByIdAndAssertObjective_IsSubmittable_DeadlineIsNotPassed(Long objectiveId) {
        Optional<Objective> objectiveOptional = objectiveRepository.findById(objectiveId);
        if (objectiveOptional.isEmpty()) {
            throw new SubmissionException(SubmissionApiError.OBJECTIVE_IS_NOT_FOUND);
        }

        Objective objective = objectiveOptional.get();
        assertObjective_IsSubmittable_DeadlineIsNotPassed(objective);
        return objective;
    }

    void assertObjective_IsSubmittable_DeadlineIsNotPassed(Objective objective) {
        if (!objective.getSubmittable()) {
            throw new SubmissionException(SubmissionApiError.OBJECTIVE_IS_NOT_SUBMITTABLE);
        }
        if (objective.getDeadline().isBefore(Instant.now())) {
            throw new SubmissionException(SubmissionApiError.OBJECTIVE_DEADLINE_HAS_PASSED);
        }

    }

    void assertInitiatorUserIsMemberOrLeaderOfItsTeam() {
        if (initiatorUserAcc.getTeam() == null || initiatorUserAcc.getTeamRole().equals(TeamRole.NOTHING)) {
            throw new SubmissionException(SubmissionApiError.INITIATOR_IS_NOT_IN_A_TEAM);
        }

        if (!initiatorUserAcc.getTeamRole().isMemberOrLeader()) {
            throw new SubmissionException(SubmissionApiError.INITIATOR_IS_NOT_MEMBER_OR_LEADER_OF_THE_TEAM);
        }
    }
}
