package com.laboschqpa.server.service.apiclient;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class ApiCallerFactory {
    private final WebClient webClient;

    @Value("${auth.interservice.key}")
    private String authInterServiceKey;

    public ApiCaller createGeneral(String apiBaseUrl) {
        return new ApiCaller(apiBaseUrl, webClient);
    }

    public ApiCaller createForAuthInterService(String apiBaseUrl) {
        return new ApiCaller(apiBaseUrl, webClient, authInterServiceKey);
    }
}
