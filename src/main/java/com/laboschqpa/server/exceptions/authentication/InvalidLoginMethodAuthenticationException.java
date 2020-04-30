package com.laboschqpa.server.exceptions.authentication;

import org.springframework.security.core.AuthenticationException;

public class InvalidLoginMethodAuthenticationException extends AuthenticationException {

    public InvalidLoginMethodAuthenticationException(String message) {
        super(message);
    }

    public InvalidLoginMethodAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
