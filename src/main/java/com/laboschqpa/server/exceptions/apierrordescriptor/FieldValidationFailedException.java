package com.laboschqpa.server.exceptions.apierrordescriptor;

import com.laboschqpa.server.enums.apierrordescriptor.FieldValidationFailedApiError;
import com.laboschqpa.server.model.FieldValidationError;
import lombok.Getter;

import java.util.List;

public class FieldValidationFailedException extends ApiErrorDescriptorException {
    @Getter
    private List<FieldValidationError> fieldValidationErrors;

    public FieldValidationFailedException(List<FieldValidationError> inputFieldErrors) {
        this(inputFieldErrors, null);
    }

    public FieldValidationFailedException(List<FieldValidationError> inputFieldErrors, String message) {
        super(FieldValidationFailedApiError.FIELD_VALIDATION_FAILED, message);
        this.fieldValidationErrors = inputFieldErrors;
    }

    @Override
    public Object getPayload() {
        return this.getFieldValidationErrors();
    }
}
