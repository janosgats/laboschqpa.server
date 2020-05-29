package com.laboschqpa.server.api.errorhandling;

import com.laboschqpa.server.annotation.ApiErrorCategory;

public interface ApiErrorDescriptor {
    default String getApiErrorCategory() {
        ApiErrorCategory apiErrorCategory = this.getClass().getAnnotation(ApiErrorCategory.class);
        return apiErrorCategory != null ? apiErrorCategory.value() : null;
    }

    Integer getApiErrorCode();

    String getApiErrorName();
}
