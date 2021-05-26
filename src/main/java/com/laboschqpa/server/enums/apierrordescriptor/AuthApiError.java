package com.laboschqpa.server.enums.apierrordescriptor;

import com.laboschqpa.server.annotation.ApiErrorCategory;
import com.laboschqpa.server.api.errorhandling.ApiErrorDescriptor;

@ApiErrorCategory("auth")
public enum AuthApiError implements ApiErrorDescriptor {
    OAUTH2_AUTHORIZATION_REQUEST_FROM_ALREADY_LOGGED_IN_USER(1),
    AUTH_CORRUPTED_CONTEXT(3),
    AUTH_DEFECTIVE_AUTH_PROVIDER_RESPONSE(4),
    AUTH_EMAIL_GOT_FROM_OAUTH2_RESPONSE_BELONGS_TO_ANOTHER_ACCOUNT(5),
    AUTH_INVALID_LOGIN_METHOD(6),
    CANNOT_FIND_EXISTING_ACCOUNT_TO_LOG_IN(7),
    AUTH_USER_ACCOUNT_IS_DISABLED(8),
    AUTH_GENERIC_FAILURE(9),
    AUTH_EXTERNAL_ACCOUNT_GOT_FROM_OAUTH2_RESPONSE_BELONGS_TO_ANOTHER_ACCOUNT(10);

    private Integer apiErrorCode;

    AuthApiError(Integer apiErrorCode) {
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
