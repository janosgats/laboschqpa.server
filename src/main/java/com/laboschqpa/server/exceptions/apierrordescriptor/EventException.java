package com.laboschqpa.server.exceptions.apierrordescriptor;

import com.laboschqpa.server.enums.apierrordescriptor.EventApiError;

public class EventException extends ApiErrorDescriptorException {
    public EventException(EventApiError eventApiError) {
        super(eventApiError);
    }

    public EventException(EventApiError eventApiError, Throwable cause) {
        super(eventApiError, cause);
    }

    public EventException(EventApiError eventApiError, String message) {
        this(eventApiError, message, null);
    }

    public EventException(EventApiError eventApiError, String message, Object payload) {
        super(eventApiError, message, payload);
    }
}
