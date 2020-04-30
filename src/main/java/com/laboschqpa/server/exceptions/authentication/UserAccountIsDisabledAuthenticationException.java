package com.laboschqpa.server.exceptions.authentication;

import org.springframework.security.core.AuthenticationException;

public class UserAccountIsDisabledAuthenticationException extends AuthenticationException {

    public UserAccountIsDisabledAuthenticationException(String message) {
        super(message);
    }

    public UserAccountIsDisabledAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
