package com.laboschqpa.server.exceptions.apierrordescriptor;

import com.laboschqpa.server.enums.apierrordescriptor.RegistrationApiError;

public class RegistrationException extends ApiErrorDescriptorException {
    public RegistrationException(RegistrationApiError registrationApiError) {
        super(registrationApiError);
    }

    public RegistrationException(RegistrationApiError registrationApiError, String message) {
        this(registrationApiError, message, null);
    }

    public RegistrationException(RegistrationApiError registrationApiError, String message, Object payload) {
        super(registrationApiError, message, payload);
    }
}
