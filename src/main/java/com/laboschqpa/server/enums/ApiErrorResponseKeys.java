package com.laboschqpa.server.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.laboschqpa.server.exceptions.NotImplementedException;

import java.util.Arrays;
import java.util.Optional;

public enum ApiErrorResponseKeys {
    OAUTH2_AUTHORIZATION_REQUEST_FROM_ALREADY_LOGGED_IN_USER("OAUTH2_AUTHORIZATION_REQUEST_FROM_ALREADY_LOGGED_IN_USER");

    private String errorResponseKey;

    ApiErrorResponseKeys(String errorResponseKey) {
        this.errorResponseKey = errorResponseKey;
    }

    @JsonValue
    public String getErrorResponseKey() {
        return errorResponseKey;
    }

    public static ApiErrorResponseKeys fromErrorResponseKey(String errorResponseKey) {
        Optional<ApiErrorResponseKeys> optional = Arrays.stream(ApiErrorResponseKeys.values())
                .filter(en -> en.getErrorResponseKey().equals(errorResponseKey))
                .findFirst();

        if (optional.isEmpty())
            throw new NotImplementedException("Enum from this value is not implemented");

        return optional.get();
    }
}
