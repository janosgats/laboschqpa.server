package com.laboschqpa.server.api.errorhandling;

import lombok.Data;

@Data
public class ApiErrorResponseBody {
    private String message;
    private String key;

    public ApiErrorResponseBody(String message) {
        this.message = message;
    }

    public ApiErrorResponseBody(String message, String key) {
        this.message = message;
        this.key = key;
    }
}
