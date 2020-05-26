package com.laboschqpa.server.api.service;

import com.laboschqpa.server.api.dto.ugc.submission.CreateNewSubmissionDto;
import com.laboschqpa.server.api.dto.ugc.submission.EditSubmissionDto;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.usergeneratedcontent.Objective;
import com.laboschqpa.server.entity.usergeneratedcontent.Submission;
import com.laboschqpa.server.enums.errorkey.InvalidAttachmentApiError;
import com.laboschqpa.server.exceptions.ContentNotFoundApiException;
import com.laboschqpa.server.exceptions.ugc.InvalidAttachmentException;
import com.laboschqpa.server.repo.usergeneratedcontent.SubmissionRepository;
import com.laboschqpa.server.statemachine.StateMachineFactory;
import com.laboschqpa.server.statemachine.SubmissionStateMachine;
import com.laboschqpa.server.util.AttachmentHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubmissionService {
    private final TransactionTemplate transactionTemplate;
    private final SubmissionRepository submissionRepository;
    private final StateMachineFactory stateMachineFactory;
    private final AttachmentHelper attachmentHelper;

    public Submission getSubmission(Long submissionId) {
        Optional<Submission> submissionOptional = submissionRepository.findByIdWithEagerAttachments(submissionId);

        if (submissionOptional.isEmpty())
            throw new ContentNotFoundApiException("Cannot find Submission with Id: " + submissionId);

        return submissionOptional.get();
    }

    public void createNewSubmission(CreateNewSubmissionDto createNewSubmissionDto, UserAcc initiatorUserAcc) {
        attachmentHelper.assertAllFilesExistAndAvailableOnFileHost(createNewSubmissionDto.getAttachments());

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            public void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                SubmissionStateMachine stateMachine = stateMachineFactory.buildSubmissionStateMachine(initiatorUserAcc);
                stateMachine.createNewSubmission(createNewSubmissionDto);
            }
        });
    }

    public void editSubmission(EditSubmissionDto editSubmissionDto, UserAcc initiatorUserAcc) {
        attachmentHelper.assertAllFilesExistAndAvailableOnFileHost(editSubmissionDto.getAttachments());

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
}
