package com.laboschqpa.server.exceptions.apierrordescriptor;

import com.laboschqpa.server.enums.apierrordescriptor.RiddleApiError;

public class RiddleException extends ApiErrorDescriptorException {
    public RiddleException(RiddleApiError riddleApiError) {
        super(riddleApiError);
    }

    public RiddleException(RiddleApiError riddleApiError, String message) {
        super(riddleApiError, message);
    }
}
