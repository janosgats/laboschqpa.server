package com.laboschqpa.server.api.service;

import com.laboschqpa.server.api.dto.ugc.submission.CreateNewSubmissionDto;
import com.laboschqpa.server.api.dto.ugc.submission.DisplayListSubmissionRequest;
import com.laboschqpa.server.api.dto.ugc.submission.EditSubmissionDto;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.usergeneratedcontent.Submission;
import com.laboschqpa.server.exceptions.apierrordescriptor.ContentNotFoundException;
import com.laboschqpa.server.repo.usergeneratedcontent.SubmissionRepository;
import com.laboschqpa.server.statemachine.StateMachineFactory;
import com.laboschqpa.server.statemachine.SubmissionStateMachine;
import com.laboschqpa.server.util.AttachmentHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubmissionService {
    private final TransactionTemplate transactionTemplate;
    private final SubmissionRepository submissionRepository;
    private final StateMachineFactory stateMachineFactory;
    private final AttachmentHelper attachmentHelper;

    public Submission getSubmissionWithEagerDisplayEntities(Long submissionId) {
        Optional<Submission> submissionOptional = submissionRepository.findByIdWithEagerDisplayEntities(submissionId);

        if (submissionOptional.isEmpty())
            throw new ContentNotFoundException("Cannot find Submission with Id: " + submissionId);

        return submissionOptional.get();
    }

    public Submission createNewSubmission(CreateNewSubmissionDto createNewSubmissionDto, UserAcc initiatorUserAcc) {
        attachmentHelper.assertAllFilesAvailableAndHaveOwnerUserOf(createNewSubmissionDto.getAttachments(), initiatorUserAcc.getId());

        return transactionTemplate.execute(new TransactionCallback<Submission>() {
            @Override
            public Submission doInTransaction(TransactionStatus transactionStatus) {
                SubmissionStateMachine stateMachine = stateMachineFactory.buildSubmissionStateMachine(initiatorUserAcc);
                return stateMachine.createNewSubmission(createNewSubmissionDto);
            }
        });
    }

    public void editSubmission(EditSubmissionDto editSubmissionDto, UserAcc initiatorUserAcc) {
        attachmentHelper.assertAllFilesAvailableAndHaveOwnerUserOf(editSubmissionDto.getAttachments(), initiatorUserAcc.getId());

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            public void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                SubmissionStateMachine stateMachine = stateMachineFactory.buildSubmissionStateMachine(initiatorUserAcc);
                stateMachine.editSubmission(editSubmissionDto);
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
}
