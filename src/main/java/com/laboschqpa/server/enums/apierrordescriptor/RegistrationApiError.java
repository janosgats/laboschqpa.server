package com.laboschqpa.server.enums.apierrordescriptor;

import com.laboschqpa.server.annotation.ApiErrorCategory;
import com.laboschqpa.server.api.errorhandling.ApiErrorDescriptor;

@ApiErrorCategory("registration")
public enum RegistrationApiError implements ApiErrorDescriptor {
    E_MAIL_ADDRESS_IS_ALREADY_IN_THE_SYSTEM(1),
    REGISTRATION_REQUEST_CANNOT_BE_FOUND(2),
    REGISTRATION_REQUEST_IS_IN_AN_INVALID_PHASE(3),
    REGISTRATION_REQUEST_KEY_IS_NOT_MATCHING(4);

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
