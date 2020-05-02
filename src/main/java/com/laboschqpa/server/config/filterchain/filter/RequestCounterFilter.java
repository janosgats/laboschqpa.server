package com.laboschqpa.server.config.filterchain.filter;

import com.timgroup.statsd.StatsDClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.*;
import java.io.IOException;

@RequiredArgsConstructor
@Service
public class RequestCounterFilter implements Filter {
    private static final String STATSD_REQUEST_COUNT_METRIC_NAME = "request.count";
    private static final Integer STATSD_SEND_INTERVAL = 20;//To produce less UDP requests
    private final StatsDClient statsDClient;

    private int requestIncomingCountInCurrentInterval = 0;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ++requestIncomingCountInCurrentInterval;
        if (requestIncomingCountInCurrentInterval >= STATSD_SEND_INTERVAL) {
            statsDClient.count(STATSD_REQUEST_COUNT_METRIC_NAME, requestIncomingCountInCurrentInterval);
            requestIncomingCountInCurrentInterval = 0;
        }

        chain.doFilter(request, response);
    }
}
