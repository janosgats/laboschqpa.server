package com.laboschqpa.server.exceptions.apierrordescriptor;

import com.laboschqpa.server.api.errorhandling.ApiErrorDescriptor;
import lombok.Getter;

public class ApiErrorDescriptorException extends RuntimeException {
    @Getter
    private ApiErrorDescriptor apiErrorDescriptor;

    private Object payload;

    public ApiErrorDescriptorException(ApiErrorDescriptor apiErrorDescriptor) {
        this(apiErrorDescriptor, null);
    }

    public ApiErrorDescriptorException(ApiErrorDescriptor apiErrorDescriptor, String message) {
        this(apiErrorDescriptor, message, null);
    }

    public ApiErrorDescriptorException(ApiErrorDescriptor apiErrorDescriptor, String message, Object payload) {
        super(message);
        this.apiErrorDescriptor = apiErrorDescriptor;
        this.payload = payload;
    }

    /**
     * Feel free to override this getter instead of passing a payload into the constructor!
     */
    public Object getPayload() {
        return payload;
    }
}
