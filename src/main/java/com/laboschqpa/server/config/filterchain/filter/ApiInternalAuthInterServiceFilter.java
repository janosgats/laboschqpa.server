package com.laboschqpa.server.config.filterchain.filter;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.laboschqpa.server.config.helper.AppConstants;
import com.laboschqpa.server.exceptions.UnAuthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ApiInternalAuthInterServiceFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(ApiInternalAuthInterServiceFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        boolean canRequestProcessingBeContinued = false;
        try {
            canRequestProcessingBeContinued = decideIfRequestProcessingCanBeContinued(httpServletRequest, response, chain);
        } catch (UnAuthorizedException e) {
            logger.trace("Request is unauthorized in AuthFilter: " + e.getMessage());
            writeErrorResponseBody((HttpServletResponse) response, "Unauthorized: " + e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            logger.debug("Exception thrown while trying to authenticate incoming request!", e);
            writeErrorResponseBody((HttpServletResponse) response, "Exception thrown while trying to authenticate incoming request!", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (canRequestProcessingBeContinued) {
            chain.doFilter(request, response);
        }
    }

    private boolean decideIfRequestProcessingCanBeContinued(HttpServletRequest httpServletRequest, ServletResponse response, FilterChain chain) {
        if (httpServletRequest.getRequestURI().contains(
                AppConstants.apiInternalUrl.substring(1, AppConstants.apiInternalUrl.length() - 1))) {
            logger.trace("Request path indicates that AuthInterService is required: {}", httpServletRequest.getRequestURI());

            String authInterServiceHeader = httpServletRequest.getHeader("AuthInterService");
            if (isAuthInterServiceHeaderValid(authInterServiceHeader)) {
                return true;
            } else {
                throw new UnAuthorizedException("AuthInterService header is invalid.");
            }
        } else {
            logger.trace("AuthInterService is NOT required. URL: {}", httpServletRequest.getRequestURI());
            return true;
        }
    }

    private boolean isAuthInterServiceHeaderValid(String authHeader) {
        return authHeader != null
                && !authHeader.isBlank()
                && authHeader.equals(System.getProperty("auth.interservice.key"));
    }

    private void writeErrorResponseBody(HttpServletResponse httpServletResponse, String errorMessage, HttpStatus httpStatus) throws IOException {
        ObjectNode responseObjectNode = new ObjectNode(JsonNodeFactory.instance);
        responseObjectNode.put("error", errorMessage);
        String responseBody = responseObjectNode.toString();

        httpServletResponse.setContentType("application/json");
        httpServletResponse.setContentLength(responseBody.length());
        httpServletResponse.getWriter().write(responseBody);
        httpServletResponse.setStatus(httpStatus.value());
    }
}
