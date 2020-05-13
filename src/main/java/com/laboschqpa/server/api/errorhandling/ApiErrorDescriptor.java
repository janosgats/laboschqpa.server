package com.laboschqpa.server.api.errorhandling;

import com.laboschqpa.server.annotation.ApiErrorType;

public interface ApiErrorDescriptor {
    default String getApiErrorType() {
        ApiErrorType apiErrorType = this.getClass().getAnnotation(ApiErrorType.class);
        return apiErrorType != null ? apiErrorType.value() : null;
    }

    Integer getApiErrorCode();

    String getApiErrorName();
}
