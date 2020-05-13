package com.laboschqpa.server.config.filterchain.extension;

import com.laboschqpa.server.api.errorhandling.ApiErrorResponseBody;
import com.laboschqpa.server.enums.errorkey.AuthApiError;
import com.laboschqpa.server.exceptions.authentication.*;
import com.laboschqpa.server.util.ServletHelper;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Log4j2
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        AuthApiError responseKey;
        String errorMessage = exception.getMessage();
        if (exception instanceof CannotFindExistingAccountAndNoRegistrationSessionDataIsSetAuthenticationException) {
            responseKey = AuthApiError.AUTHN_CANNOT_FIND_EXISTING_ACCOUNT_AND_NO_REGISTRATION_SESSION_DATA_IS_SET;
        } else if (exception instanceof CorruptedContextAuthenticationException) {
            responseKey = AuthApiError.AUTHN_CORRUPTED_CONTEXT;
            log.warn("Authentication failure", exception);
        } else if (exception instanceof DefectiveAuthProviderResponseAuthenticationException) {
            responseKey = AuthApiError.AUTHN_DEFECTIVE_AUTH_PROVIDER_RESPONSE;
        } else if (exception instanceof EmailGotFromOAuth2ResponseBelongsToAnOtherAccountAuthenticationException) {
            responseKey = AuthApiError.AUTHN_EMAIL_GOT_FROM_OAUTH2_RESPONSE_BELONGS_TO_ANOTHER_ACCOUNT;
        } else if (exception instanceof InvalidLoginMethodAuthenticationException) {
            responseKey = AuthApiError.AUTHN_INVALID_LOGIN_METHOD;
            log.warn("Authentication failure", exception);
        } else if (exception instanceof RegistrationRequestReferredBySessionDataIsInvalidAuthenticationException) {
            responseKey = AuthApiError.AUTHN_REGISTRATION_REQUEST_REFERRED_BY_SESSION_DATA_IS_INVALID;
        } else if (exception instanceof UserAccountIsDisabledAuthenticationException) {
            responseKey = AuthApiError.AUTHN_USER_ACCOUNT_IS_DISABLED;
        } else {
            log.info("Authentication failure", exception);
            responseKey = AuthApiError.AUTHN_GENERIC_FAILURE;
            errorMessage = String.format("%s: %s", exception.getClass().getSimpleName(), exception.getMessage());
        }

        setJoinFlowExceptionResponse(response, responseKey, errorMessage);
    }

    private void setJoinFlowExceptionResponse(ServletResponse response, AuthApiError errorResponseKey, String errorMessage) {
        ServletHelper.setJsonResponse((HttpServletResponse) response,
                new ApiErrorResponseBody(errorResponseKey, errorMessage),
                409);
    }
}
