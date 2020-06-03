package com.laboschqpa.server.api.errorhandling;

import com.laboschqpa.server.enums.apierrordescriptor.FieldValidationFailedApiError;
import com.laboschqpa.server.exceptions.*;
import com.laboschqpa.server.exceptions.apierrordescriptor.ApiErrorDescriptorException;
import com.laboschqpa.server.util.ConstraintHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.Collection;

@ControllerAdvice
public class ExceptionHandlerControllerAdvice extends ResponseEntityExceptionHandler {
    private static final Logger loggerOfChild = LoggerFactory.getLogger(ExceptionHandlerControllerAdvice.class);

    private final ApiErrorResponseBody unAuthorizedErrorResponseBody = new ApiErrorResponseBody("You are not authorized for the requested operation.");
    private final ApiErrorResponseBody cannotParseIncomingHttpRequestErrorResponseBody = new ApiErrorResponseBody("Error while executing API request.");

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        loggerOfChild.debug("Cannot parse incoming HTTP message!", ex);

        return new ResponseEntity<>(cannotParseIncomingHttpRequestErrorResponseBody, headers, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    protected ResponseEntity<ApiErrorResponseBody> handleConstraintViolationException(
            ConstraintViolationException e, WebRequest request) {
        loggerOfChild.trace("handleConstraintViolationException() caught exception while executing api request!", e);
        return new ResponseEntity<>(
                new ApiErrorResponseBody(FieldValidationFailedApiError.FIELD_VALIDATION_FAILED, e.getMessage(),
                        ConstraintHelper.convertConstraintViolationsToFieldValidationErrors((Collection) e.getConstraintViolations())
                ), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({ApiErrorDescriptorException.class})
    protected ResponseEntity<ApiErrorResponseBody> handleApiErrorDescriptorException(
            ApiErrorDescriptorException e, WebRequest request) {
        loggerOfChild.trace("handleApiErrorDescriptorException() caught exception while executing api request!", e);
        return new ResponseEntity<>(new ApiErrorResponseBody(e), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UnAuthorizedException.class)
    protected ResponseEntity<ApiErrorResponseBody> handleUnAuthorized(
            Exception e, WebRequest request) {
        loggerOfChild.debug("UnAuthorizedException caught while executing api request!", e);
        return new ResponseEntity<>(unAuthorizedErrorResponseBody, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiErrorResponseBody> handleGenericException(
            Exception e, WebRequest request) {
        loggerOfChild.error("Exception caught while executing api request!", e);
        return new ResponseEntity<>(new ApiErrorResponseBody(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
