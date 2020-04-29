package com.laboschqpa.server.config.filterchain;

import com.laboschqpa.server.api.errorhandling.ApiErrorResponseBody;
import com.laboschqpa.server.enums.ApiErrorResponseKeys;
import com.laboschqpa.server.util.ServletHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

public class AddLoginMethodFilter implements Filter {
    private static final String REGISTRATION_ID_URI_VARIABLE_NAME = "registrationId";
    private final AntPathRequestMatcher oAuth2AuthorizationRequestMatcher;

    public AddLoginMethodFilter(final String oAuth2AuthorizationRequestBaseUri) {
        oAuth2AuthorizationRequestMatcher = new AntPathRequestMatcher(
                oAuth2AuthorizationRequestBaseUri + "/{" + REGISTRATION_ID_URI_VARIABLE_NAME + "}");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        if (oAuth2AuthorizationRequestMatcher.matches(httpServletRequest)
                && SecurityContextHolder.getContext().getAuthentication() != null) {
            handleRequestSentToOAuth2AuthorizationEndpointByLoggedInUser(httpServletRequest, httpServletResponse, chain);
        } else {
            chain.doFilter(request, response);
        }
    }

    private void handleRequestSentToOAuth2AuthorizationEndpointByLoggedInUser(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        boolean userExplicitlyWantsToAddLoginMethod = Objects.equals(request.getParameter("addLoginMethod"), "true");
        if (userExplicitlyWantsToAddLoginMethod) {
            chain.doFilter(request, response);
        } else {
            ServletHelper.setJsonResponse(response,
                    new ApiErrorResponseBody("You are already logged in! You have to explicitly specify if you want to add a new login method.",
                            ApiErrorResponseKeys.OAUTH2_AUTHORIZATION_REQUEST_FROM_ALREADY_LOGGED_IN_USER.getErrorResponseKey()),
                    409);
        }
    }
}
