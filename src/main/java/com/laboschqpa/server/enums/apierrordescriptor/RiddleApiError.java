package com.laboschqpa.server.enums.apierrordescriptor;

import com.laboschqpa.server.annotation.ApiErrorCategory;
import com.laboschqpa.server.api.errorhandling.ApiErrorDescriptor;

@ApiErrorCategory("riddle")
public enum RiddleApiError implements ApiErrorDescriptor {
    RIDDLE_IS_NOT_FOUND(2),
    REQUESTED_RIDDLE_IS_NOT_YET_ACCESSIBLE_FOR_YOUR_TEAM(3),
    YOUR_TEAM_ALREADY_SOLVED_THE_RIDDLE(4),
    A_RIDDLE_HAS_TO_HAVE_EXACTLY_ONE_ATTACHMENT(5),
    TEAM_RATE_LIMIT_HIT_FOR_RIDDLE_SUBMISSIONS(6);

    private Integer apiErrorCode;

    RiddleApiError(Integer apiErrorCode) {
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
