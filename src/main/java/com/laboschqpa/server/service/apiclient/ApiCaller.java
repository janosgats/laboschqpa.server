package com.laboschqpa.server.service.apiclient;

import com.fasterxml.jackson.databind.JsonNode;
import com.laboschqpa.server.config.helper.AppConstants;
import com.laboschqpa.server.exceptions.apiclient.ApiClientException;
import com.laboschqpa.server.exceptions.apiclient.ResponseCodeIsNotSuccessApiClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

public class ApiCaller {
    private static final Logger logger = LoggerFactory.getLogger(ApiCaller.class);

    private String apiBaseUrl;
    private WebClient webClient;
    private final boolean useAuthInterService;
    private String authInterServiceKey;

    public ApiCaller(String apiBaseUrl, WebClient webClient) {
        this.useAuthInterService = false;
        this.apiBaseUrl = apiBaseUrl;
        this.webClient = webClient;
    }

    public ApiCaller(String apiBaseUrl, WebClient webClient, String authInterServiceKey) {
        this.useAuthInterService = true;
        this.apiBaseUrl = apiBaseUrl;
        this.webClient = webClient;
        this.authInterServiceKey = authInterServiceKey;
    }

    public <T> T doCallAndThrowExceptionIfStatuscodeIsNot2xx(final Class<T> responseBodyClass, String uriPath, HttpMethod httpMethod) {
        return doCallAndThrowExceptionIfStatuscodeIsNot2xx(responseBodyClass, uriPath, httpMethod, null, null);
    }

    public <T> T doCallAndThrowExceptionIfStatuscodeIsNot2xx(final Class<T> responseBodyClass, String uriPath, HttpMethod httpMethod, Map<String, String> queryParams) {
        return doCallAndThrowExceptionIfStatuscodeIsNot2xx(responseBodyClass, uriPath, httpMethod, queryParams, null);
    }

    public <T> T doCallAndThrowExceptionIfStatuscodeIsNot2xx(final Class<T> responseBodyClass, String uriPath, HttpMethod httpMethod,
                                                             BodyInserter<? extends Object, ReactiveHttpOutputMessage> requestBodyInserter) {
        return doCallAndThrowExceptionIfStatuscodeIsNot2xx(responseBodyClass, uriPath, httpMethod, null, requestBodyInserter, null);
    }

    public <T> T doCallAndThrowExceptionIfStatuscodeIsNot2xx(final Class<T> responseBodyClass, String uriPath, HttpMethod httpMethod,
                                                             Map<String, String> queryParams, JsonNode requestBody) {
        HttpHeaders httpHeaders = new HttpHeaders();
        BodyInserter<?, ? super ClientHttpRequest> requestBodyInserter;

        if (requestBody == null) {
            requestBodyInserter = BodyInserters.empty();
        } else {
            requestBodyInserter = BodyInserters.fromValue(requestBody.toString());
            httpHeaders.add("Content-Type", "application/json");
        }

        return doCallAndThrowExceptionIfStatuscodeIsNot2xx(responseBodyClass, uriPath, httpMethod, queryParams, requestBodyInserter, httpHeaders, false);
    }

    public <T> T doCallAndThrowExceptionIfStatuscodeIsNot2xx(final Class<T> responseBodyClass, String uriPath, HttpMethod httpMethod, HttpHeaders headers) {

        return doCallAndThrowExceptionIfStatuscodeIsNot2xx(responseBodyClass, uriPath, httpMethod, null, BodyInserters.empty(), headers, false);
    }

    public <T> T doCallAndThrowExceptionIfStatuscodeIsNot2xx(final Class<T> responseBodyClass, String uriPath, HttpMethod httpMethod, Map<String, String> queryParams,
                                                             BodyInserter<? extends Object, ReactiveHttpOutputMessage> requestBodyInserter, HttpHeaders headers) {

        return doCallAndThrowExceptionIfStatuscodeIsNot2xx(responseBodyClass, uriPath, httpMethod, queryParams, requestBodyInserter, headers, false);
    }

    public <T> T doCallAndThrowExceptionIfStatuscodeIsNot2xx(final Class<T> responseBodyClass, String uriPath, HttpMethod httpMethod, Map<String, String> queryParams,
                                                             String stringRequestBody, HttpHeaders headers) {
        return doCallAndThrowExceptionIfStatuscodeIsNot2xx(responseBodyClass, uriPath, httpMethod, queryParams,
                BodyInserters.fromValue(stringRequestBody), headers, false);
    }

    public <T> T doCallAndThrowExceptionIfStatuscodeIsNot2xx(final Class<T> responseBodyClass, String uriPath, HttpMethod httpMethod, Map<String, String> queryParams,
                                                             BodyInserter<?, ? super ClientHttpRequest> requestBodyInserter, HttpHeaders headers, final boolean disableUrlEncodingOfQueryParams) {
        if (headers == null) {
            return doCallAndThrowExceptionIfStatuscodeIsNot2xx(responseBodyClass, uriPath, httpMethod, queryParams,
                    requestBodyInserter, new HttpHeaders(), disableUrlEncodingOfQueryParams);
        }

        if (this.useAuthInterService) {
            headers.add(AppConstants.authInterServiceHeaderName, authInterServiceKey);
        }

        if (requestBodyInserter == null)
            requestBodyInserter = BodyInserters.empty();

        String queryString = createQueryStringFromHttpParameters(queryParams, disableUrlEncodingOfQueryParams);
        String fullUriString = this.apiBaseUrl + uriPath + "?" + queryString;

        ClientResponse response = this.webClient
                .method(httpMethod)
                .uri(fullUriString)
                .body(requestBodyInserter)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .exchange()
                .onErrorResume(e -> {
                    logger.error("Error during communication with " + fullUriString, e);
                    return Mono.error(new ApiClientException(e));
                })
                .block();

        if (response.statusCode().is2xxSuccessful()) {
            return handleWhenResponseCodeIsSuccess(response, httpMethod, fullUriString, responseBodyClass);
        } else {
            handleWhenResponseCodeIndicatesFailure(response, httpMethod, fullUriString);
            throw new IllegalStateException("This code shouldn't be reached!");
        }
    }

    protected <T> T handleWhenResponseCodeIsSuccess(ClientResponse response, HttpMethod httpMethod, String fullUriString, final Class<T> responseBodyClass) {
        try {
            T responseBody = response.bodyToMono(responseBodyClass).block();
            logger.trace("Rest call succeeded. Url: {{} {}}, HTTP status code: {{}}",
                    httpMethod,
                    fullUriString,
                    response.statusCode()
            );

            return responseBody;
        } catch (Exception e) {
            logger.debug("Exception while getting response body. Expected body class type: {}, Url: {{} {}}, HTTP status code: {{}}",
                    responseBodyClass.getSimpleName(),
                    httpMethod,
                    fullUriString,
                    response.statusCode()
            );
            throw e;
        }
    }

    protected void handleWhenResponseCodeIndicatesFailure(ClientResponse response, HttpMethod httpMethod, String fullUriString) {
        String responseBodyString = response.bodyToMono(String.class).block();
        logger.error("HTTP status code indicates failure. Url: {{} {}}, HTTP status code: {{}}",
                httpMethod,
                fullUriString,
                response.statusCode()
        );
        throw ResponseCodeIsNotSuccessApiClientException.builder()
                .message("HTTP status code is not 2xx success")
                .httpStatus(response.statusCode())
                .responseBody(responseBodyString)
                .build();
    }

    protected String createQueryStringFromHttpParameters(final Map<String, String> queryParams, final boolean disableUrlEncodingOfQueryParams) {
        if (queryParams == null) {
            return "";
        } else {
            return queryParams.keySet().stream()
                    .map((key) -> generateKeyValuePairForHttpQueryString(queryParams, key, disableUrlEncodingOfQueryParams))
                    .collect(Collectors.joining("&"));
        }
    }

    private String generateKeyValuePairForHttpQueryString(final Map<String, String> allQueryParams, final String keyToGeneratePairFor, final boolean disableUrlEncodingOfQueryParams) {
        String value = allQueryParams.get(keyToGeneratePairFor);
        if (value == null) {
            return String.format("%s=", keyToGeneratePairFor);
        }

        if (disableUrlEncodingOfQueryParams) {
            return String.format("%s=%s", keyToGeneratePairFor, value);
        } else {
            return String.format("%s=%s", keyToGeneratePairFor, URLEncoder.encode(value, StandardCharsets.UTF_8));
        }
    }
}