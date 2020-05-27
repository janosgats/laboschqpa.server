package com.laboschqpa.server.enums.apierrordescriptor;

import com.laboschqpa.server.annotation.ApiErrorType;
import com.laboschqpa.server.api.errorhandling.ApiErrorDescriptor;

@ApiErrorType("invalidAttachment")
public enum InvalidAttachmentApiError implements ApiErrorDescriptor {
    SOME_FILES_ARE_NOT_AVAILABLE(1);

    private Integer apiErrorCode;

    InvalidAttachmentApiError(Integer errorResponseNumber) {
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
