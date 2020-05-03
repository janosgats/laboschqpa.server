package com.laboschqpa.server.service.resources;

import com.timgroup.statsd.StatsDClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Log4j2
@RequiredArgsConstructor
@Service
public class ResourcesReporterService implements Runnable {
    private final StatsDClient statsDClient;

    @PostConstruct
    private void startUp() {
        log.info("Available processors: {}", Runtime.getRuntime().availableProcessors());
    }

    @Override
    public void run() {
        final long usedMemoryBytes = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        statsDClient.gauge("resources.memory.used", usedMemoryBytes);
    }
}
