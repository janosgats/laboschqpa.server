package com.laboschqpa.server.api.errorhandling;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiErrorResponseBody {
    private Integer apiErrorCode;
    private String apiErrorName;
    private String apiErrorType;

    private String message;

    public ApiErrorResponseBody(String message) {
        this(null, message);
    }

    public ApiErrorResponseBody(ApiErrorDescriptor apiErrorDescriptor) {
        this(apiErrorDescriptor, null);
    }

    public ApiErrorResponseBody(ApiErrorDescriptor apiErrorDescriptor, String message) {
        if (apiErrorDescriptor != null) {
            this.apiErrorType = apiErrorDescriptor.getApiErrorType();
            this.apiErrorCode = apiErrorDescriptor.getApiErrorCode();
            this.apiErrorName = apiErrorDescriptor.getApiErrorName();
        }

        this.message = message;
    }
}
