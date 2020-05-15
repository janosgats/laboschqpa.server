package com.laboschqpa.server.api.service;

import com.laboschqpa.server.api.dto.submission.CreateNewSubmissionDto;
import com.laboschqpa.server.api.dto.submission.EditSubmissionDto;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.usergeneratedcontent.Submission;
import com.laboschqpa.server.repo.SubmissionRepository;
import com.laboschqpa.server.statemachine.StateMachineFactory;
import com.laboschqpa.server.statemachine.SubmissionStateMachine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmissionService {
    private final TransactionTemplate transactionTemplate;
    private final SubmissionRepository submissionRepository;
    private final StateMachineFactory stateMachineFactory;

    public void createNewSubmission(CreateNewSubmissionDto createNewSubmissionDto, UserAcc initiatorUserAcc) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            public void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                SubmissionStateMachine stateMachine = stateMachineFactory.buildSubmissionStateMachine(initiatorUserAcc);
                stateMachine.createNewSubmission(createNewSubmissionDto);
            }
        });
    }

    public void editSubmission(EditSubmissionDto editSubmissionDto, UserAcc initiatorUserAcc) {
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
