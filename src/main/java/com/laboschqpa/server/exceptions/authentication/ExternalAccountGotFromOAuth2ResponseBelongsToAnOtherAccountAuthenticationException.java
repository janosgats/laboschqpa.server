package com.laboschqpa.server.exceptions.authentication;

import org.springframework.security.core.AuthenticationException;

public class ExternalAccountGotFromOAuth2ResponseBelongsToAnOtherAccountAuthenticationException extends AuthenticationException {

    public ExternalAccountGotFromOAuth2ResponseBelongsToAnOtherAccountAuthenticationException(String message) {
        super(message);
    }

    public ExternalAccountGotFromOAuth2ResponseBelongsToAnOtherAccountAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
