package com.laboschqpa.server.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.laboschqpa.server.exceptions.apierrordescriptor.FieldValidationFailedException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Collection;

public abstract class SelfValidator {
    private Validator validator;

    /**
     * Get Lazy-initialized validator
     */
    private Validator getValidator() {
        if (validator == null) {
            final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            validator = factory.getValidator();
        }
        return validator;
    }

    public void validateSelf() {
        Collection<ConstraintViolation> violations = (Collection) getValidator().validate(this);
        if (!violations.isEmpty()) {
            throw new FieldValidationFailedException(ConstraintHelper.convertConstraintViolationsToFieldValidationErrors(violations));
        }
    }

    @JsonIgnore
    public boolean isValid() {
        Collection<ConstraintViolation> violations = (Collection) getValidator().validate(this);
        return violations.isEmpty();
    }
}
