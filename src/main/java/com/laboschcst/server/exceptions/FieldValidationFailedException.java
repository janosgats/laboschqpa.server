package com.laboschcst.server.exceptions;

import com.laboschcst.server.model.FieldValidationError;
import lombok.Data;

import java.util.List;

@Data
public class FieldValidationFailedException extends RuntimeException {

    private List<FieldValidationError> fieldValidationErrors;

    public FieldValidationFailedException(List<FieldValidationError> inputFieldErrors) {
        this.fieldValidationErrors = inputFieldErrors;
    }
}
