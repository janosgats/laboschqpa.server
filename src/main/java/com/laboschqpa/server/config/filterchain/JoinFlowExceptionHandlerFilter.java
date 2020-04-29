package com.laboschqpa.server.config.filterchain;

import com.laboschqpa.server.exceptions.NotImplementedException;
import com.laboschqpa.server.exceptions.joinflow.JoinFlowException;

import javax.servlet.*;
import java.io.IOException;

public class JoinFlowExceptionHandlerFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (JoinFlowException e) {
            throw new NotImplementedException("Implement this!!!", e);
        }
    }
}
