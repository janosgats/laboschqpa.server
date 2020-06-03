package com.laboschqpa.server.util;

import com.laboschqpa.server.model.FieldValidationError;

import javax.validation.ConstraintViolation;
import java.util.Collection;
import java.util.stream.Collectors;

public class ConstraintHelper {

    public static Collection<FieldValidationError> convertConstraintViolationsToFieldValidationErrors(Collection<ConstraintViolation> constraintViolations) {
        if (constraintViolations == null) {
            return null;
        }

        return constraintViolations.stream()
                .map(violation -> new FieldValidationError(
                        violation.getPropertyPath().toString(),
                        violation.getMessage()
                )).collect(Collectors.toList());
    }

}
