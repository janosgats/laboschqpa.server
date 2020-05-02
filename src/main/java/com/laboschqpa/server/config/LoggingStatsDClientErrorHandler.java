package com.laboschqpa.server.config;

import com.timgroup.statsd.StatsDClientErrorHandler;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class LoggingStatsDClientErrorHandler implements StatsDClientErrorHandler {
    @Override
    public void handle(Exception exception) {
        log.warn("StatsD Client error: ", exception);
    }
}
