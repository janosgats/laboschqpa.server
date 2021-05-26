package com.laboschqpa.server.exceptions.authentication;

import org.springframework.security.core.AuthenticationException;

public class CannotFindExistingAccountToLogInAuthenticationException extends AuthenticationException {

    public CannotFindExistingAccountToLogInAuthenticationException(String message) {
        super(message);
    }

    public CannotFindExistingAccountToLogInAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
