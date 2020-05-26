package com.laboschqpa.server.statemachine;

import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.repo.usergeneratedcontent.ObjectiveRepository;
import com.laboschqpa.server.repo.usergeneratedcontent.SubmissionRepository;
import com.laboschqpa.server.repo.UserAccRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StateMachineFactory {
    private final UserAccRepository userAccRepository;
    private final ObjectiveRepository objectiveRepository;
    private final SubmissionRepository submissionRepository;

    public TeamUserRelationStateMachine buildTeamUserRelationStateMachine(UserAcc alteredUserAcc, UserAcc initiatorUserAcc) {
        return new TeamUserRelationStateMachine(alteredUserAcc, initiatorUserAcc, userAccRepository);
    }

    public SubmissionStateMachine buildSubmissionStateMachine(UserAcc initiatorUserAcc) {
        return new SubmissionStateMachine(initiatorUserAcc, objectiveRepository, submissionRepository);
    }
}
