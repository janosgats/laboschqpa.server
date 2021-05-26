package com.laboschqpa.server.exceptions.authentication;

import org.springframework.security.core.AuthenticationException;

public class EmailBelongsToAnOtherAccountAuthenticationException extends AuthenticationException {

    public EmailBelongsToAnOtherAccountAuthenticationException(String message) {
        super(message);
    }

    public EmailBelongsToAnOtherAccountAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
