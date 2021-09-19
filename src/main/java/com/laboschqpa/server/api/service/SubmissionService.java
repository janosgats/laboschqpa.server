package com.laboschqpa.server.api.service;

import com.laboschqpa.server.api.dto.ugc.submission.CreateNewSubmissionDto;
import com.laboschqpa.server.api.dto.ugc.submission.DisplayListSubmissionRequest;
import com.laboschqpa.server.api.dto.ugc.submission.EditSubmissionRequest;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.usergeneratedcontent.Submission;
import com.laboschqpa.server.enums.auth.Authority;
import com.laboschqpa.server.exceptions.apierrordescriptor.ContentNotFoundException;
import com.laboschqpa.server.repo.usergeneratedcontent.SubmissionRepository;
import com.laboschqpa.server.statemachine.StateMachineFactory;
import com.laboschqpa.server.statemachine.SubmissionStateMachine;
import com.laboschqpa.server.util.AttachmentHelper;
import com.laboschqpa.server.util.CollectionHelpers;
import com.laboschqpa.server.util.PrincipalAuthorizationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SubmissionService {
    private final TransactionTemplate transactionTemplate;
    private final SubmissionRepository submissionRepository;
    private final StateMachineFactory stateMachineFactory;
    private final AttachmentHelper attachmentHelper;

    @Value("${users.hideSubmissionsWithoutAcceptedEmail:true}")
    private Boolean hideSubmissionsWithoutAcceptedEmail;

    public Submission getSubmissionWithEagerDisplayEntities(Long submissionId) {
        Optional<Submission> submissionOptional = submissionRepository.findByIdWithEagerDisplayEntities(submissionId);

        if (submissionOptional.isEmpty())
            throw new ContentNotFoundException("Cannot find Submission with Id: " + submissionId);

        return submissionOptional.get();
    }

    public Submission createNewSubmission(CreateNewSubmissionDto createNewSubmissionDto, UserAcc initiatorUserAcc) {
        attachmentHelper.assertAllFilesAvailableAndHaveOwnerUserOf(createNewSubmissionDto.getAttachments(), initiatorUserAcc);

        return transactionTemplate.execute(new TransactionCallback<Submission>() {
            @Override
            public Submission doInTransaction(TransactionStatus transactionStatus) {
                SubmissionStateMachine stateMachine = stateMachineFactory.buildSubmissionStateMachine(initiatorUserAcc);
                return stateMachine.createNewSubmission(createNewSubmissionDto);
            }
        });
    }

    public void editSubmission(EditSubmissionRequest request, UserAcc initiatorUserAcc) {
        Submission editedSubmission = submissionRepository.findByIdWithEagerAttachments(request.getId())
                .orElseThrow(() -> new ContentNotFoundException("Cannot find Submission with Id: " + request.getId()));

        final HashSet<Long> newlyAddedAttachments = CollectionHelpers.subtractToSet(request.getAttachments(), editedSubmission.getAttachments());
        attachmentHelper.assertAllFilesAvailableAndHaveOwnerUserOf(newlyAddedAttachments, initiatorUserAcc);

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            public void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                SubmissionStateMachine stateMachine = stateMachineFactory.buildSubmissionStateMachine(initiatorUserAcc);
                stateMachine.editSubmission(request);
            }
        });
    }

    public void deleteSubmission(Long submissionIdToDelete, UserAcc initiatorUserAcc) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            public void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                SubmissionStateMachine stateMachine = stateMachineFactory.buildSubmissionStateMachine(initiatorUserAcc);
                stateMachine.deleteSubmission(submissionIdToDelete);
            }
        });
    }

    public List<Submission> listAll() {
        return submissionRepository.findAll();
    }

    public List<Submission> listWithEagerDisplayEntities(DisplayListSubmissionRequest request) {
        final Long filteredTeamId = request.getTeamId();
        final Long filteredObjectiveId = request.getObjectiveId();

        if (filteredTeamId != null && filteredObjectiveId != null) {
            return submissionRepository.findByObjectiveIdAndTeamId_withEagerDisplayEntities_orderByCreationTimeDesc(filteredObjectiveId, filteredTeamId);
        }

        if (filteredObjectiveId != null) {
            return submissionRepository.findByObjectiveId_withEagerDisplayEntities_orderByCreationTimeDesc(filteredObjectiveId);
        }

        if (filteredTeamId != null) {
            return submissionRepository.findByTeamId_withEagerDisplayEntities_orderByCreationTimeDesc(filteredTeamId);
        }

        return submissionRepository.findAll_withEagerDisplayEntities_orderByCreationTimeDesc();
    }

    public List<Submission> filterSubmissionsThatUserCanSee(List<Submission> submissionsToFilter,
                                                            UserAcc userAcc) {
        if (new PrincipalAuthorizationHelper(userAcc).hasAnySufficientAuthority(Authority.TeamScorer)) {
            return submissionsToFilter;
        }

        final Instant now = Instant.now();
        final Long userId = userAcc.getId();

        Long teamIdWithMembership = null;
        if (userAcc.getTeamRole().isMemberOrLeader() && userAcc.getTeam() != null) {
            teamIdWithMembership = userAcc.getTeam().getId();
        }

        final List<Submission> visibleSubmissions = new ArrayList<>();
        for (Submission submission : submissionsToFilter) {
            if (Objects.equals(userId, submission.getCreatorUser().getId())) {
                //Anyone can see their own submissions
                visibleSubmissions.add(submission);
                continue;
            }
            if (hideSubmissionsWithoutAcceptedEmail && !userAcc.getIsAcceptedByEmail()) {
                //Not accepted user -> can't see anything but their own submissions
                continue;
            }
            if (Objects.equals(teamIdWithMembership, submission.getTeam().getId())) {
                //Submission by current team
                visibleSubmissions.add(submission);
                continue;
            }
            if (submission.getObjective().getHideSubmissionsBefore() == null) {
                //Always public submission
                visibleSubmissions.add(submission);
                continue;
            }
            if (now.isAfter(submission.getObjective().getHideSubmissionsBefore())) {
                //The submission is now public
                visibleSubmissions.add(submission);
                continue;
            }
            //Not visible by default
        }

        return visibleSubmissions;
    }
}
