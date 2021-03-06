package com.laboschqpa.server.enums.apierrordescriptor;

import com.laboschqpa.server.annotation.ApiErrorCategory;
import com.laboschqpa.server.api.errorhandling.ApiErrorDescriptor;

@ApiErrorCategory("registration")
public enum RegistrationApiError implements ApiErrorDescriptor {
    NO_REGISTRATION_INFO_FOUND_IN_SESSION(1),
    USER_ACCOUNT_ALREADY_EXISTS(2);

    private Integer apiErrorCode;

    RegistrationApiError(Integer apiErrorCode) {
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
