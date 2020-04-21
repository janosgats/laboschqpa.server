package com.laboschqpa.server.exceptions;

public class UsersAdminException extends RuntimeException {
    public UsersAdminException() {
    }

    public UsersAdminException(String message) {
        super(message);
    }

    public UsersAdminException(String message, Throwable cause) {
        super(message, cause);
    }

    public UsersAdminException(Throwable cause) {
        super(cause);
    }

    public UsersAdminException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
