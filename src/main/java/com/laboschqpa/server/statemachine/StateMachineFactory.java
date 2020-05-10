package com.laboschqpa.server.statemachine;

import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.repo.UserAccRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StateMachineFactory {
    private final UserAccRepository userAccRepository;

    public TeamUserRelationStateMachine buildTeamUserRelationStateMachine(UserAcc alteredUserAcc, UserAcc initiatorUserAcc) {
        return new TeamUserRelationStateMachine(alteredUserAcc, initiatorUserAcc, userAccRepository);
    }
}
