package com.laboschqpa.server.util;

import com.laboschqpa.server.exceptions.FieldValidationFailedException;
import com.laboschqpa.server.model.FieldValidationError;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class SelfValidator<T extends SelfValidator<T>> {
    private Validator validator;

    public SelfValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public void validateSelf() {
        Set<ConstraintViolation<T>> violations = validator.validate((T) this);
        if (!violations.isEmpty()) {
            throw new FieldValidationFailedException(violations.stream()
                    .map(violation -> new FieldValidationError(violation.getPropertyPath().toString(), violation.getMessage()))
                    .collect(Collectors.toList()));
        }
    }

    public boolean isValid() {
        Set<ConstraintViolation<T>> violations = validator.validate((T) this);
        return violations.isEmpty();
    }
}
