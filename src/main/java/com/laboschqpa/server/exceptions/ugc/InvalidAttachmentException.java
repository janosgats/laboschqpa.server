package com.laboschqpa.server.exceptions.ugc;

import com.laboschqpa.server.enums.errorkey.InvalidAttachmentApiError;
import lombok.Getter;

public class InvalidAttachmentException extends RuntimeException {
    @Getter
    private InvalidAttachmentApiError invalidAttachmentApiError = null;

    public InvalidAttachmentException(InvalidAttachmentApiError invalidAttachmentApiError) {
        this.invalidAttachmentApiError = invalidAttachmentApiError;
    }

    public InvalidAttachmentException(String message, InvalidAttachmentApiError invalidAttachmentApiError) {
        super(message);
        this.invalidAttachmentApiError = invalidAttachmentApiError;
    }

    public InvalidAttachmentException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidAttachmentException(Throwable cause) {
        super(cause);
    }

    public InvalidAttachmentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
