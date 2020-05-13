package com.laboschqpa.server.enums.errorkey;

import com.laboschqpa.server.annotation.ApiErrorType;
import com.laboschqpa.server.api.errorhandling.ApiErrorDescriptor;

@ApiErrorType("submission")
public enum SubmissionApiError implements ApiErrorDescriptor {
    INITIATOR_IS_NOT_IN_A_TEAM(1),
    INITIATOR_IS_NOT_MEMBER_OR_LEADER_OF_THE_TEAM(2),
    OBJECTIVE_IS_NOT_FOUND(3),
    OBJECTIVE_IS_NOT_SUBMITTABLE(4),
    OBJECTIVE_DEADLINE_HAS_PASSED(5),
    YOU_HAVE_TO_BE_TEAM_LEADER_TO_MODIFY_THE_SUBMISSION_OF_SOMEONE_ELSE(6),
    YOU_CANNOT_MODIFY_A_SUBMISSION_IF_YOU_ARE_NOT_IN_THE_SUBMITTER_TEAM(7),
    SUBMISSION_IS_NOT_FOUND(8);

    private Integer apiErrorCode;

    SubmissionApiError(Integer errorResponseNumber) {
        this.apiErrorCode = errorResponseNumber;
    }

    @Override
    public Integer getApiErrorCode() {
        return apiErrorCode;
    }

    @Override
    public String getApiErrorName() {
        return toString();
    }
}
