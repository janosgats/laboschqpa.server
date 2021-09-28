package com.laboschqpa.server.enums.apierrordescriptor;

import com.laboschqpa.server.annotation.ApiErrorCategory;
import com.laboschqpa.server.api.errorhandling.ApiErrorDescriptor;

@ApiErrorCategory("qrFight")
public enum QrFightApiError implements ApiErrorDescriptor {
    YOUR_TEAM_ALREADY_SUBMITTED_THIS_TAG(1),
    TAG_DOES_NOT_EXIST(2),
    TAG_SECRET_MISMATCH(3),
    TEAM_RATE_LIMIT_HIT_FOR_QR_FIGHT_SUBMISSIONS(4),
    FIGHT_AREA_IS_NOT_ENABLED(5);

    private Integer apiErrorCode;

    QrFightApiError(Integer apiErrorCode) {
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
