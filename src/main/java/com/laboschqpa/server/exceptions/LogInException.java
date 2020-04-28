package com.laboschqpa.server.exceptions;

public class LogInException extends RuntimeException {
    public LogInException() {
    }

    public LogInException(String message) {
        super(message);
    }

    public LogInException(String message, Throwable cause) {
        super(message, cause);
    }

    public LogInException(Throwable cause) {
        super(cause);
    }

    public LogInException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
