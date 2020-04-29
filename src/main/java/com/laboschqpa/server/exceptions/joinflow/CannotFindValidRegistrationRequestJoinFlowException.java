package com.laboschqpa.server.exceptions.joinflow;

public class CannotFindValidRegistrationRequestJoinFlowException extends JoinFlowException {
    public CannotFindValidRegistrationRequestJoinFlowException() {
    }

    public CannotFindValidRegistrationRequestJoinFlowException(String message) {
        super(message);
    }

    public CannotFindValidRegistrationRequestJoinFlowException(String message, Throwable cause) {
        super(message, cause);
    }

    public CannotFindValidRegistrationRequestJoinFlowException(Throwable cause) {
        super(cause);
    }

    public CannotFindValidRegistrationRequestJoinFlowException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
