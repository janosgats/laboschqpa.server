package com.laboschqpa.server.exceptions.apierrordescriptor;

import com.laboschqpa.server.enums.apierrordescriptor.InvalidAttachmentApiError;

public class InvalidAttachmentException extends ApiErrorDescriptorException {
    public InvalidAttachmentException(InvalidAttachmentApiError invalidAttachmentApiError) {
        super(invalidAttachmentApiError);
    }

    public InvalidAttachmentException(InvalidAttachmentApiError invalidAttachmentApiError, String message) {
        super(invalidAttachmentApiError, message);
    }
}
