package com.laboschqpa.server.exceptions.authentication;

import org.springframework.security.core.AuthenticationException;

public class CannotFindExistingAccountAndNoRegistrationSessionDataIsSetAuthenticationException extends AuthenticationException {

    public CannotFindExistingAccountAndNoRegistrationSessionDataIsSetAuthenticationException(String message) {
        super(message);
    }

    public CannotFindExistingAccountAndNoRegistrationSessionDataIsSetAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
