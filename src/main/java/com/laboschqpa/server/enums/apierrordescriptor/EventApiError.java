package com.laboschqpa.server.enums.apierrordescriptor;

import com.laboschqpa.server.annotation.ApiErrorCategory;
import com.laboschqpa.server.api.errorhandling.ApiErrorDescriptor;

@ApiErrorCategory("event")
public enum EventApiError implements ApiErrorDescriptor {
    EVENT_IS_NOT_PERSONAL_EVENT(1),
    EVENT_IS_NOT_TEAM_EVENT(2),
    DEADLINE_HAS_PASSED(3),
    REGISTRATION_LIMIT_EXCEEDED(4),
    ONLY_LEADERS_CAN_MANAGE_TEAM_EVENT_REGISTRATIONS(5),
    ALREADY_REGISTERED(6);

    private Integer apiErrorCode;

    EventApiError(Integer apiErrorCode) {
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
