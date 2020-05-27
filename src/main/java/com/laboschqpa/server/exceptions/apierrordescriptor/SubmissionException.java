package com.laboschqpa.server.exceptions.apierrordescriptor;

import com.laboschqpa.server.enums.apierrordescriptor.SubmissionApiError;

public class SubmissionException extends ApiErrorDescriptorException {
    public SubmissionException(SubmissionApiError submissionApiError) {
        super(submissionApiError);
    }

    public SubmissionException(SubmissionApiError submissionApiError, String message) {
        super(submissionApiError, message);
    }
}
