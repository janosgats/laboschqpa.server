package com.laboschqpa.server.config;

import com.laboschqpa.server.service.resources.ResourcesReporterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class ContextStartedEventListener implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger logger = LoggerFactory.getLogger(ContextStartedEventListener.class);

    private static volatile boolean wasGlobalResourcesReporterStarted = false;
    private static final Object globalResourcesReporterStartingLock = new Object();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextStartedEvent) {
        synchronized (globalResourcesReporterStartingLock) {
            if (!wasGlobalResourcesReporterStarted) {
                logger.info("Starting ResourcesReporterService.");
                ResourcesReporterService resourcesReporterService = contextStartedEvent.getApplicationContext().getBean(ResourcesReporterService.class);
                Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(resourcesReporterService, 0, 10, TimeUnit.SECONDS);
                wasGlobalResourcesReporterStarted = true;
            }
        }
    }
}