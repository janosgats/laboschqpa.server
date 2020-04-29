package com.laboschqpa.server.exceptions.joinflow;

public class JoinFlowException extends RuntimeException {
    public JoinFlowException() {
    }

    public JoinFlowException(String message) {
        super(message);
    }

    public JoinFlowException(String message, Throwable cause) {
        super(message, cause);
    }

    public JoinFlowException(Throwable cause) {
        super(cause);
    }

    public JoinFlowException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
