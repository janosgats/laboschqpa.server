package com.laboschqpa.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.MimeType;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;

@Configuration
public class AppConfig {
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .codecs(configurer -> {
                    ObjectMapper objectMapper = configurer.getReaders().stream()
                            .filter(reader -> reader instanceof Jackson2JsonDecoder)
                            .map(reader -> (Jackson2JsonDecoder) reader)
                            .map(reader -> reader.getObjectMapper())
                            .findFirst()
                            .orElseGet(() -> Jackson2ObjectMapperBuilder.json().build());

                    Jackson2JsonDecoder decoder = new Jackson2JsonDecoder(objectMapper, new MimeType("text", "json", StandardCharsets.UTF_8));
                    configurer.customCodecs().registerWithDefaultConfig(decoder);
                })
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}