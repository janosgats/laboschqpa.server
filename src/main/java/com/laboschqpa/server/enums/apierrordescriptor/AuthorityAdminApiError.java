package com.laboschqpa.server.enums.apierrordescriptor;

import com.laboschqpa.server.annotation.ApiErrorCategory;
import com.laboschqpa.server.api.errorhandling.ApiErrorDescriptor;

@ApiErrorCategory("authorityAdmin")
public enum AuthorityAdminApiError implements ApiErrorDescriptor {
    THE_ALTERED_USER_ALREADY_HAS_THE_AUTHORITY(1),
    THE_ALTERED_USER_DOES_NOT_HAVE_THE_AUTHORITY(2);

    private Integer apiErrorCode;

    AuthorityAdminApiError(Integer apiErrorCode) {
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
