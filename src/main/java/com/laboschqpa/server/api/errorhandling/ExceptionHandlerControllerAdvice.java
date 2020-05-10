package com.laboschqpa.server.api.errorhandling;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laboschqpa.server.exceptions.*;
import com.laboschqpa.server.exceptions.joinflow.RegistrationJoinFlowException;
import com.laboschqpa.server.model.FieldValidationError;
import lombok.Data;
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
import java.util.List;

@ControllerAdvice
public class ExceptionHandlerControllerAdvice extends ResponseEntityExceptionHandler {
    private static final Logger loggerOfChild = LoggerFactory.getLogger(ExceptionHandlerControllerAdvice.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final ApiErrorResponseBody contentNotFoundErrorResponseBody = new ApiErrorResponseBody("Content not found.");
    private final ApiErrorResponseBody conflictingRequestDataErrorResponseBody = new ApiErrorResponseBody("Conflicting request data.");
    private final ApiErrorResponseBody unAuthorizedErrorResponseBody = new ApiErrorResponseBody("You are not authorized for the requested operation.");
    private final ApiErrorResponseBody genericExceptionErrorResponseBody = new ApiErrorResponseBody("Error while executing API request.");
    private final ApiErrorResponseBody cannotParseIncomingHttpRequestErrorResponseBody = new ApiErrorResponseBody("Error while executing API request.");

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        loggerOfChild.debug("Cannot parse incoming HTTP message!", ex);

        return new ResponseEntity<>(cannotParseIncomingHttpRequestErrorResponseBody, headers, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FieldValidationFailedException.class)
    protected ResponseEntity<FieldValidationFailedApiResponse> handleFieldValidationFailed(
            FieldValidationFailedException ex, WebRequest request) {
        try {
            loggerOfChild.trace("InputFieldsNeedWorkException handled in ControllerAdvice: " + objectMapper.writeValueAsString(ex.getFieldValidationErrors()));
        } catch (JsonProcessingException e) {
            loggerOfChild.trace("InputFieldsNeedWorkException handled in ControllerAdvice.");
        }

        return new ResponseEntity<>(
                new FieldValidationFailedApiResponse(ex.getFieldValidationErrors()),
                HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(ContentNotFoundApiException.class)
    protected ResponseEntity<ApiErrorResponseBody> handleContentNotFound(
            Exception e, WebRequest request) {
        loggerOfChild.trace("ContentNotFoundApiException caught while executing api request!", e);
        return new ResponseEntity<>(
                new ApiErrorResponseBody(contentNotFoundErrorResponseBody.getMessage() + " - " + e.getMessage()),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler({TeamUserRelationException.class,
            RegistrationJoinFlowException.class,
            ConstraintViolationException.class})
    protected ResponseEntity<ApiErrorResponseBody> handleClientCausedErrors(
            Exception e, WebRequest request) {
        loggerOfChild.trace("handleClientCausedErrors() caught exception while executing api request!", e);
        return new ResponseEntity<>(new ApiErrorResponseBody(e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ConflictingRequestDataApiException.class)
    protected ResponseEntity<ApiErrorResponseBody> handleConflictingRequestData(
            Exception e, WebRequest request) {
        loggerOfChild.trace("ConflictingRequestDataApiException caught while executing api request!", e);
        return new ResponseEntity<>(conflictingRequestDataErrorResponseBody, HttpStatus.CONFLICT);
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
        return new ResponseEntity<>(genericExceptionErrorResponseBody, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Data
    public static class FieldValidationFailedApiResponse {
        private List<FieldValidationError> fieldValidationErrors;

        public FieldValidationFailedApiResponse(List<FieldValidationError> fieldErrors) {
            this.fieldValidationErrors = fieldErrors;
        }
    }
}
