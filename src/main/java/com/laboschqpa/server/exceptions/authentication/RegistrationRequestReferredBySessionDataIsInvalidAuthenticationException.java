package com.laboschqpa.server.exceptions.authentication;

import org.springframework.security.core.AuthenticationException;

public class RegistrationRequestReferredBySessionDataIsInvalidAuthenticationException extends AuthenticationException {

    public RegistrationRequestReferredBySessionDataIsInvalidAuthenticationException(String message) {
        super(message);
    }

    public RegistrationRequestReferredBySessionDataIsInvalidAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
