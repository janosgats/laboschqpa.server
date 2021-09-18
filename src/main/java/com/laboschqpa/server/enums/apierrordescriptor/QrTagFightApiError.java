package com.laboschqpa.server.enums.apierrordescriptor;

import com.laboschqpa.server.annotation.ApiErrorCategory;
import com.laboschqpa.server.api.errorhandling.ApiErrorDescriptor;

@ApiErrorCategory("qrTagFight")
public enum QrTagFightApiError implements ApiErrorDescriptor {
    YOUR_TEAM_ALREADY_SUBMITTED_THIS_TAG(1),
    TAG_DOES_NOT_EXIST(2),
    TAG_SECRET_MISMATCH(3);

    private Integer apiErrorCode;

    QrTagFightApiError(Integer apiErrorCode) {
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
