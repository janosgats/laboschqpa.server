package com.laboschqpa.server.exceptions;

public class InvalidAuthenticationPrincipalException extends RuntimeException {
    public InvalidAuthenticationPrincipalException() {
    }

    public InvalidAuthenticationPrincipalException(String message) {
        super(message);
    }

    public InvalidAuthenticationPrincipalException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidAuthenticationPrincipalException(Throwable cause) {
        super(cause);
    }

    public InvalidAuthenticationPrincipalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
