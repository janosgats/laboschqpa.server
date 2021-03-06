package com.laboschqpa.server.config.filterchain.extension;

import com.laboschqpa.server.api.errorhandling.ApiErrorResponseBody;
import com.laboschqpa.server.enums.apierrordescriptor.AuthApiError;
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
        if (exception instanceof CorruptedContextAuthenticationException) {
            responseKey = AuthApiError.AUTH_CORRUPTED_CONTEXT;
            log.warn("Authentication failure", exception);
        } else if (exception instanceof DefectiveAuthProviderResponseAuthenticationException) {
            responseKey = AuthApiError.AUTH_DEFECTIVE_AUTH_PROVIDER_RESPONSE;
        } else if (exception instanceof EmailBelongsToAnOtherAccountAuthenticationException) {
            responseKey = AuthApiError.AUTH_EMAIL_GOT_FROM_OAUTH2_RESPONSE_BELONGS_TO_ANOTHER_ACCOUNT;
        } else if (exception instanceof ExternalAccountGotFromOAuth2ResponseBelongsToAnOtherAccountAuthenticationException) {
            responseKey = AuthApiError.AUTH_EXTERNAL_ACCOUNT_GOT_FROM_OAUTH2_RESPONSE_BELONGS_TO_ANOTHER_ACCOUNT;
        } else if (exception instanceof InvalidLoginMethodAuthenticationException) {
            responseKey = AuthApiError.AUTH_INVALID_LOGIN_METHOD;
            log.warn("Authentication failure", exception);
        } else if (exception instanceof CannotFindExistingAccountToLogInAuthenticationException) {
            responseKey = AuthApiError.CANNOT_FIND_EXISTING_ACCOUNT_TO_LOG_IN;
        } else if (exception instanceof UserAccountIsDisabledAuthenticationException) {
            responseKey = AuthApiError.AUTH_USER_ACCOUNT_IS_DISABLED;
        } else {
            log.info("Authentication failure", exception);
            responseKey = AuthApiError.AUTH_GENERIC_FAILURE;
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
