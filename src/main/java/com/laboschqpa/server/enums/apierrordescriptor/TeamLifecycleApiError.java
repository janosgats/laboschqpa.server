package com.laboschqpa.server.enums.apierrordescriptor;

import com.laboschqpa.server.annotation.ApiErrorCategory;
import com.laboschqpa.server.api.errorhandling.ApiErrorDescriptor;

@ApiErrorCategory("teamLifecycle")
public enum TeamLifecycleApiError implements ApiErrorDescriptor {
    INITIATOR_IS_DIFFERENT_THAN_ALTERED(1),
    INITIATOR_IS_NOT_LEADER_OF_TEAM_OF_ALTERED(2),
    INITIATOR_IS_SAME_AS_ALTERED(3),
    YOU_HAVE_TO_BE_A_LEADER_TO_DO_THIS_OPERATION(4),
    THERE_IS_NO_OTHER_LEADER(5),
    OPERATION_IS_INVALID_FOR_TEAM_ROLE_OF_ALTERED(6),
    YOU_ARE_ALREADY_MEMBER_OR_APPLICANT_OF_A_TEAM(7),
    EXITING_FROM_TEAM_IS_NOT_ALLOWED(8),
    CREATION_OF_NEW_TEAMS_IS_DISABLED(9),
    THIS_TEAM_NAME_IS_ALREADY_TAKEN(10);

    private Integer apiErrorCode;

    TeamLifecycleApiError(Integer apiErrorCode) {
        this.apiErrorCode = apiErrorCode;
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
