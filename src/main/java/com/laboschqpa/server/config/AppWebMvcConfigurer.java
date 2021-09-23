package com.laboschqpa.server.config;

import com.laboschqpa.server.enums.converter.ObjectiveTypeFromValueMvcConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppWebMvcConfigurer implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new ObjectiveTypeFromValueMvcConverter());
    }
}
