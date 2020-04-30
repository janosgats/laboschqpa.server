package com.laboschqpa.server.exceptions.joinflow;

public class RegistrationJoinFlowException extends JoinFlowException {
    public RegistrationJoinFlowException() {
    }

    public RegistrationJoinFlowException(String message) {
        super(message);
    }

    public RegistrationJoinFlowException(String message, Throwable cause) {
        super(message, cause);
    }

    public RegistrationJoinFlowException(Throwable cause) {
        super(cause);
    }

    public RegistrationJoinFlowException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
