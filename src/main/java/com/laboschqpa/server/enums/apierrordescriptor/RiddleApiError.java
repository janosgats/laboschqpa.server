package com.laboschqpa.server.enums.apierrordescriptor;

import com.laboschqpa.server.annotation.ApiErrorType;
import com.laboschqpa.server.api.errorhandling.ApiErrorDescriptor;

@ApiErrorType("riddle")
public enum RiddleApiError implements ApiErrorDescriptor {
    YOU_ARE_NOT_IN_A_TEAM(1),
    RIDDLE_IS_NOT_FOUND(2),
    REQUESTED_RIDDLE_IS_NOT_YET_ACCESSIBLE_FOR_YOUR_TEAM(3);

    private Integer apiErrorCode;

    RiddleApiError(Integer errorResponseNumber) {
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
