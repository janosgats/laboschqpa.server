package com.laboschqpa.server.config;

import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {
    @Value("${appName}")
    private String appName;

    @Value("${appconfig.statsd.host}")
    private String statsdHost;
    @Value("${appconfig.statsd.port}")
    private Integer statsdPort;

    @Bean
    public StatsDClient statsDClient() {
        return new NonBlockingStatsDClient(appName, statsdHost, statsdPort, new LoggingStatsDClientErrorHandler());
    }

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> commonTags() {
        return r -> r.config().commonTags("appName", appName, "actuatorPrefix", "actuator");
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}