package com.laboschqpa.server.enums.apierrordescriptor;

import com.laboschqpa.server.annotation.ApiErrorCategory;
import com.laboschqpa.server.api.errorhandling.ApiErrorDescriptor;

@ApiErrorCategory("objectiveAcceptance")
public enum ObjectiveAcceptanceApiError implements ApiErrorDescriptor {
    OBJECTIVE_IS_NOT_FOUND(1),
    TEAM_IS_NOT_FOUND(2),
    OBJECTIVE_IS_ALREADY_ACCEPTED(3),
    OBJECTIVE_IS_ALREADY_NOT_ACCEPTED(4);

    private Integer apiErrorCode;

    ObjectiveAcceptanceApiError(Integer apiErrorCode) {
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
