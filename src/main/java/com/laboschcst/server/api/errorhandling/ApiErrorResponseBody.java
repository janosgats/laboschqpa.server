package com.laboschcst.server.api.errorhandling;

import lombok.Data;

@Data
public class ApiErrorResponseBody {
    private String error;

    public ApiErrorResponseBody(String error) {
        this.error = error;
    }
}
