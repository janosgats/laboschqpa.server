package com.laboschqpa.server.util;

import com.laboschqpa.server.exceptions.apierrordescriptor.FieldValidationFailedException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Collection;

public abstract class SelfValidator {
    private final Validator validator;

    public SelfValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public void validateSelf() {
        Collection<ConstraintViolation> violations = (Collection) validator.validate(this);
        if (!violations.isEmpty()) {
            throw new FieldValidationFailedException(ConstraintHelper.convertConstraintViolationsToFieldValidationErrors(violations));
        }
    }

    public boolean isValid() {
        Collection<ConstraintViolation> violations = (Collection) validator.validate(this);
        return violations.isEmpty();
    }
}
