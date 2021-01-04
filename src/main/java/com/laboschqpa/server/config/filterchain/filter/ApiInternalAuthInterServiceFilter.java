package com.laboschqpa.server.config.filterchain.filter;

import com.laboschqpa.server.api.errorhandling.ApiErrorResponseBody;
import com.laboschqpa.server.config.helper.AppConstants;
import com.laboschqpa.server.exceptions.UnAuthorizedException;
import com.laboschqpa.server.service.authinterservice.AuthInterServiceCrypto;
import com.laboschqpa.server.util.ServletHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
@RequiredArgsConstructor
@Service
public class ApiInternalAuthInterServiceFilter implements Filter {
    private static final String HEADER_NAME_AUTH_INTER_SERVICE = "AuthInterService";

    private final AuthInterServiceCrypto authInterServiceCrypto;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        boolean canRequestProcessingBeContinued = false;
        try {
            canRequestProcessingBeContinued = decideIfRequestProcessingCanBeContinued(httpServletRequest, response, chain);
        } catch (UnAuthorizedException e) {
            writeErrorResponseBody((HttpServletResponse) response, "Unauthorized: " + e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            log.debug("Exception thrown while trying to authenticate incoming request!", e);
            writeErrorResponseBody((HttpServletResponse) response, "Exception thrown while trying to authenticate incoming request!", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (canRequestProcessingBeContinued) {
            chain.doFilter(request, response);
        }
    }

    private boolean decideIfRequestProcessingCanBeContinued(HttpServletRequest httpServletRequest, ServletResponse response, FilterChain chain) {
        if (httpServletRequest.getRequestURI().contains(
                AppConstants.apiInternalUrl.substring(1, AppConstants.apiInternalUrl.length() - 1))) {

            String authInterServiceHeader = httpServletRequest.getHeader(HEADER_NAME_AUTH_INTER_SERVICE);
            if (authInterServiceCrypto.isHeaderValid(authInterServiceHeader)) {
                log.trace("AuthInterService auth passed. URL: {}", httpServletRequest.getRequestURI());
                return true;
            } else {
                log.trace("AuthInterService auth failed. URL: {}", httpServletRequest.getRequestURI());
                throw new UnAuthorizedException("AuthInterService header is invalid.");
            }
        } else {
            log.trace("AuthInterService auth not required. URL: {}", httpServletRequest.getRequestURI());
            return true;
        }
    }

    private void writeErrorResponseBody(HttpServletResponse httpServletResponse, String errorMessage, HttpStatus httpStatus) {
        ServletHelper.setJsonResponse(httpServletResponse, new ApiErrorResponseBody(errorMessage), httpStatus.value());
    }
}
