package com.laboschqpa.server.exceptions.statemachine;

import com.laboschqpa.server.enums.errorkey.SubmissionApiError;
import lombok.Getter;

public class SubmissionException extends RuntimeException {
    @Getter
    private SubmissionApiError submissionApiError = null;

    public SubmissionException(SubmissionApiError submissionApiError) {
        this.submissionApiError = submissionApiError;
    }

    public SubmissionException(String message, SubmissionApiError submissionApiError) {
        super(message);
        this.submissionApiError = submissionApiError;
    }

    public SubmissionException(String message, Throwable cause) {
        super(message, cause);
    }

    public SubmissionException(Throwable cause) {
        super(cause);
    }

    public SubmissionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
