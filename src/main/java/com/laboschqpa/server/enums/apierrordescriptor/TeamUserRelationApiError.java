package com.laboschqpa.server.enums.apierrordescriptor;

import com.laboschqpa.server.annotation.ApiErrorCategory;
import com.laboschqpa.server.api.errorhandling.ApiErrorDescriptor;

@ApiErrorCategory("teamUserRelation")
public enum TeamUserRelationApiError implements ApiErrorDescriptor {
    INITIATOR_IS_DIFFERENT_THAN_ALTERED(1),
    INITIATOR_IS_NOT_LEADER_OF_TEAM_OF_ALTERED(2),
    INITIATOR_IS_SAME_AS_ALTERED(3),
    YOU_HAVE_TO_BE_A_LEADER_TO_DO_THIS_OPERATION(4),
    THERE_IS_NO_OTHER_LEADER(5),
    OPERATION_IS_INVALID_FOR_TEAM_ROLE_OF_ALTERED(6),
    YOU_ARE_ALREADY_MEMBER_OF_A_TEAM(7);

    private Integer apiErrorCode;

    TeamUserRelationApiError(Integer apiErrorCode) {
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
