package com.laboschqpa.server.exceptions.apierrordescriptor;

import com.laboschqpa.server.enums.apierrordescriptor.FieldValidationFailedApiError;
import com.laboschqpa.server.model.FieldValidationError;
import lombok.Getter;

import java.util.Collection;

public class FieldValidationFailedException extends ApiErrorDescriptorException {
    @Getter
    private Collection<FieldValidationError> fieldValidationErrors;

    public FieldValidationFailedException(Collection<FieldValidationError> inputFieldErrors) {
        this(inputFieldErrors, null);
    }

    public FieldValidationFailedException(Collection<FieldValidationError> inputFieldErrors, String message) {
        super(FieldValidationFailedApiError.FIELD_VALIDATION_FAILED, message);
        this.fieldValidationErrors = inputFieldErrors;
    }

    @Override
    public Object getPayload() {
        return this.getFieldValidationErrors();
    }
}
