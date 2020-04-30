package com.laboschqpa.server.exceptions.authentication;

import org.springframework.security.core.AuthenticationException;

public class EmailGotFromOAuth2ResponseBelongsToAnOtherAccountAuthenticationException extends AuthenticationException {

    public EmailGotFromOAuth2ResponseBelongsToAnOtherAccountAuthenticationException(String message) {
        super(message);
    }

    public EmailGotFromOAuth2ResponseBelongsToAnOtherAccountAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
