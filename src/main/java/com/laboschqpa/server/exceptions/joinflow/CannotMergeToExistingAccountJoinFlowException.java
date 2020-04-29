package com.laboschqpa.server.exceptions.joinflow;

public class CannotMergeToExistingAccountJoinFlowException extends JoinFlowException {
    public CannotMergeToExistingAccountJoinFlowException() {
    }

    public CannotMergeToExistingAccountJoinFlowException(String message) {
        super(message);
    }

    public CannotMergeToExistingAccountJoinFlowException(String message, Throwable cause) {
        super(message, cause);
    }

    public CannotMergeToExistingAccountJoinFlowException(Throwable cause) {
        super(cause);
    }

    public CannotMergeToExistingAccountJoinFlowException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
