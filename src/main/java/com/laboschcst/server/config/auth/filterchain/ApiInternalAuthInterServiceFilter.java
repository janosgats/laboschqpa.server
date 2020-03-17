package com.laboschcst.server.config.auth.filterchain;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.laboschcst.server.config.AppConstants;
import com.laboschcst.server.exceptions.UnAuthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(1)
public class ApiInternalAuthInterServiceFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(ApiInternalAuthInterServiceFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        try {
            if (httpServletRequest.getRequestURI().contains(
                    AppConstants.apiInternalUrl.substring(1, AppConstants.apiInternalUrl.length() - 1))) {
                logger.trace("Request path indicates that AuthInterService is required: {}", httpServletRequest.getRequestURI());

                String authInterServiceHeader = httpServletRequest.getHeader("AuthInterService");
                if (!isAuthInterServiceHeaderValid(authInterServiceHeader))
                    throw new UnAuthorizedException("AuthInterService header is invalid.");
            }

            chain.doFilter(request, response);
        } catch (UnAuthorizedException e) {
            logger.trace("Request is unauthorized in AuthFilter: " + e.getMessage());
            writeErrorResponseBody((HttpServletResponse) response, "Unauthorized: " + e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            logger.debug("Exception thrown while trying to authenticate incoming request!", e);
            writeErrorResponseBody((HttpServletResponse) response, "Exception thrown while trying to authenticate incoming request!", HttpStatus.INTERNAL_SERVER_ERROR);
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
