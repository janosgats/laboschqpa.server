package com.laboschcst.server.exceptions;

public class TeamUserRelationException extends RuntimeException {
    public TeamUserRelationException() {
    }

    public TeamUserRelationException(String message) {
        super(message);
    }

    public TeamUserRelationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TeamUserRelationException(Throwable cause) {
        super(cause);
    }

    public TeamUserRelationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
