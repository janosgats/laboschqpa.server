package com.laboschqpa.server.exceptions.authentication;

import org.springframework.security.core.AuthenticationException;

public class CorruptedContextAuthenticationException extends AuthenticationException {

    public CorruptedContextAuthenticationException(String message) {
        super(message);
    }

    public CorruptedContextAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
