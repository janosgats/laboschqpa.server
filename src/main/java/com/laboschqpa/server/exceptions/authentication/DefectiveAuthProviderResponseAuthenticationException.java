package com.laboschqpa.server.exceptions.authentication;

import org.springframework.security.core.AuthenticationException;

public class DefectiveAuthProviderResponseAuthenticationException extends AuthenticationException {

    public DefectiveAuthProviderResponseAuthenticationException(String message) {
        super(message);
    }

    public DefectiveAuthProviderResponseAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
