package com.laboschqpa.server.exceptions.apierrordescriptor;

import com.laboschqpa.server.enums.apierrordescriptor.ObjectiveAcceptanceApiError;

public class ObjectiveAcceptanceException extends ApiErrorDescriptorException {
    public ObjectiveAcceptanceException(ObjectiveAcceptanceApiError apiError) {
        super(apiError);
    }

    public ObjectiveAcceptanceException(ObjectiveAcceptanceApiError apiError, String message) {
        super(apiError, message);
    }
}
