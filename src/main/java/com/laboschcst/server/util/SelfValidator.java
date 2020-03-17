package com.laboschcst.server.util;

import com.laboschcst.server.exceptions.FieldValidationFailedException;
import com.laboschcst.server.model.FieldValidationError;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class SelfValidator<T> {
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
}
