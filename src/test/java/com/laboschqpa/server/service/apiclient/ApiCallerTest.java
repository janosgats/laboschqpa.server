package com.laboschqpa.server.service.apiclient;

import com.laboschqpa.server.config.AppConfig;
import com.laboschqpa.server.exceptions.apiclient.ResponseCodeIsNotSuccessApiClientException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
class ApiCallerTest {
    private static final String[] secretsToHideInLogs = {"testSecretToHideInLog"};
    private static final String authInterServiceKey = "testAuthInterServiceKey";

    private WebClient webClient = new AppConfig().webClient();
    private ApiCaller apiCaller;

    private static MockWebServer mockWebServer;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void beforeEach() {
        apiCaller = spy(new ApiCaller("http://localhost:" + mockWebServer.getPort(), webClient, secretsToHideInLogs, authInterServiceKey));
    }

    @Test
    void doCallAndThrowExceptionIfStatuscodeIsNot2xx_success_withResponseBody() throws InterruptedException {
        final Class<String> responseBodyClass = String.class;
        final String uriPath = "/test";
        final HttpMethod httpMethod = HttpMethod.POST;
        final Map<String, String> queryParams = Map.of("param1", "value1");
        final String requestBodyString = "request body string";
        final BodyInserter<String, ReactiveHttpOutputMessage> requestBodyInserter = BodyInserters.fromValue(requestBodyString);
        final HttpHeaders headers = new HttpHeaders();
        headers.add("key1", "value1");
        final boolean disableUrlEncodingOfQueryParams = false;

        final String expectedResponseBody = "expected response body is here";

        mockWebServer.enqueue(new MockResponse()
                .setBody(expectedResponseBody));

        final String actualResponseBody
                = apiCaller.doCallAndThrowExceptionIfStatuscodeIsNot2xx(responseBodyClass, uriPath, httpMethod,
                queryParams, requestBodyInserter, headers, disableUrlEncodingOfQueryParams);

        final RecordedRequest recordedRequest = Objects.requireNonNull(mockWebServer.takeRequest(1, TimeUnit.SECONDS));


        assertTrue(Objects.requireNonNull(recordedRequest.getPath()).startsWith(uriPath + "?"));
        assertTrue(Objects.requireNonNull(recordedRequest.getPath()).contains("param1=value1"));

        assertEquals(httpMethod.name(), recordedRequest.getMethod());
        assertEquals("value1", recordedRequest.getHeader("key1"));

        assertEquals(authInterServiceKey, recordedRequest.getHeader("AuthInterService"));

        assertEquals(requestBodyString, new String(recordedRequest.getBody().readByteArray()));
        assertEquals(expectedResponseBody, actualResponseBody);
    }

    @Test
    void doCallAndThrowExceptionIfStatuscodeIsNot2xx_success_withNoResponseBody() throws InterruptedException {
        final Class<String> responseBodyClass = String.class;
        final String uriPath = "/test";
        final HttpMethod httpMethod = HttpMethod.POST;
        final Map<String, String> queryParams = Map.of("param1", "value1");
        final String requestBodyString = "request body string";
        final BodyInserter<String, ReactiveHttpOutputMessage> requestBodyInserter = BodyInserters.fromValue(requestBodyString);
        final HttpHeaders headers = new HttpHeaders();
        headers.add("key1", "value1");
        final boolean disableUrlEncodingOfQueryParams = false;

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200));

        final String actualResponseBody
                = apiCaller.doCallAndThrowExceptionIfStatuscodeIsNot2xx(responseBodyClass, uriPath, httpMethod,
                queryParams, requestBodyInserter, headers, disableUrlEncodingOfQueryParams);

        final RecordedRequest recordedRequest = Objects.requireNonNull(mockWebServer.takeRequest(1, TimeUnit.SECONDS));


        assertTrue(Objects.requireNonNull(recordedRequest.getPath()).startsWith(uriPath + "?"));
        assertTrue(Objects.requireNonNull(recordedRequest.getPath()).contains("param1=value1"));

        assertEquals(httpMethod.name(), recordedRequest.getMethod());
        assertEquals("value1", recordedRequest.getHeader("key1"));

        assertEquals(authInterServiceKey, recordedRequest.getHeader("AuthInterService"));

        assertEquals(requestBodyString, new String(recordedRequest.getBody().readByteArray()));
        assertNull(actualResponseBody);
    }

    @Test
    void doCallAndThrowExceptionIfStatuscodeIsNot2xx_errorHttpResponseCodeIsNot2xx_withResponseBody() throws InterruptedException {
        final Class<String> responseBodyClass = String.class;
        final String uriPath = "/test";
        final HttpMethod httpMethod = HttpMethod.POST;
        final Map<String, String> queryParams = Map.of("param1", "value1");
        final String requestBodyString = "request body string";
        final BodyInserter<String, ReactiveHttpOutputMessage> requestBodyInserter = BodyInserters.fromValue(requestBodyString);
        final HttpHeaders headers = new HttpHeaders();
        headers.add("key1", "value1");
        final boolean disableUrlEncodingOfQueryParams = false;

        final HttpStatus expectedResponseCode = HttpStatus.NOT_FOUND;
        final String expectedResponseBody = "expected response body is here";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(expectedResponseCode.value())
                .setBody(expectedResponseBody));

        ResponseCodeIsNotSuccessApiClientException receivedException = null;
        try {
            apiCaller.doCallAndThrowExceptionIfStatuscodeIsNot2xx(responseBodyClass, uriPath, httpMethod,
                    queryParams, requestBodyInserter, headers, disableUrlEncodingOfQueryParams);
            fail("Nothing was thrown, but ResponseCodeIsNotSuccessApiClientException should have been thrown!");
        } catch (ResponseCodeIsNotSuccessApiClientException e) {
            receivedException = e;
        }

        mockWebServer.takeRequest(1, TimeUnit.SECONDS);

        assertEquals(expectedResponseCode, receivedException.getHttpStatus());
        assertEquals(expectedResponseBody, receivedException.getResponseBody());
    }

    @Test
    void doCallAndThrowExceptionIfStatuscodeIsNot2xx_errorHttpResponseCodeIsNot2xx_withNoResponseBody() throws InterruptedException {
        final Class<String> responseBodyClass = String.class;
        final String uriPath = "/test";
        final HttpMethod httpMethod = HttpMethod.POST;
        final Map<String, String> queryParams = Map.of("param1", "value1");
        final String requestBodyString = "request body string";
        final BodyInserter<String, ReactiveHttpOutputMessage> requestBodyInserter = BodyInserters.fromValue(requestBodyString);
        final HttpHeaders headers = new HttpHeaders();
        headers.add("key1", "value1");
        final boolean disableUrlEncodingOfQueryParams = false;

        final HttpStatus expectedResponseCode = HttpStatus.NOT_FOUND;

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(expectedResponseCode.value()));

        ResponseCodeIsNotSuccessApiClientException receivedException = null;
        try {
            apiCaller.doCallAndThrowExceptionIfStatuscodeIsNot2xx(responseBodyClass, uriPath, httpMethod,
                    queryParams, requestBodyInserter, headers, disableUrlEncodingOfQueryParams);
            fail("Nothing was thrown, but ResponseCodeIsNotSuccessApiClientException should have been thrown!");
        } catch (ResponseCodeIsNotSuccessApiClientException e) {
            receivedException = e;
        }

        mockWebServer.takeRequest(1, TimeUnit.SECONDS);

        assertEquals(expectedResponseCode, receivedException.getHttpStatus());
        assertTrue(StringUtils.isEmpty(receivedException.getResponseBody()));
    }

    @Test
    void hideSecretForLogsInString() {
        final String str = "blahblahsa" + secretsToHideInLogs[0] + "asdkdjfasd" + secretsToHideInLogs[0];

        assertFalse(apiCaller.hideSecretForLogsInString(str).contains(secretsToHideInLogs[0]));
    }
}