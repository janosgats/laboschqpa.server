package com.laboschqpa.server.exceptions.apierrordescriptor;

import com.laboschqpa.server.enums.apierrordescriptor.EmailAddressApiError;

public class EmailAddressException extends ApiErrorDescriptorException {
    public EmailAddressException(EmailAddressApiError emailAddressApiError) {
        super(emailAddressApiError);
    }

    public EmailAddressException(EmailAddressApiError emailAddressApiError, Throwable cause) {
        super(emailAddressApiError, cause);
    }

    public EmailAddressException(EmailAddressApiError emailAddressApiError, String message) {
        this(emailAddressApiError, message, null);
    }

    public EmailAddressException(EmailAddressApiError emailAddressApiError, String message, Object payload) {
        super(emailAddressApiError, message, payload);
    }
}
