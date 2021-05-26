package com.laboschqpa.server.enums.apierrordescriptor;

import com.laboschqpa.server.annotation.ApiErrorCategory;
import com.laboschqpa.server.api.errorhandling.ApiErrorDescriptor;

@ApiErrorCategory("emailAddress")
public enum EmailAddressApiError implements ApiErrorDescriptor {
    EMAIL_ALREADY_BELONGS_TO_A_USER(1),
    VERIFICATION_REQUEST_CANNOT_BE_FOUND(2),
    VERIFICATION_REQUEST_PHASE_IS_INVALID(3),
    VERIFICATION_REQUEST_KEY_IS_NOT_MATCHING(4);

    private Integer apiErrorCode;

    EmailAddressApiError(Integer apiErrorCode) {
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
