package com.laboschqpa.server.exceptions;

import com.laboschqpa.server.model.FieldValidationError;
import lombok.Data;

import java.util.List;

@Data
public class FieldValidationFailedException extends RuntimeException {

    private List<FieldValidationError> fieldValidationErrors;

    public FieldValidationFailedException(List<FieldValidationError> inputFieldErrors) {
        this.fieldValidationErrors = inputFieldErrors;
    }
}
