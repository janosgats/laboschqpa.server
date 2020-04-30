package com.laboschqpa.server.service.apiclient;

public abstract class AbstractApiClient {
    private ApiCallerFactory apiCallerFactory;

    private ApiCaller apiCaller;

    public AbstractApiClient(ApiCallerFactory apiCallerFactory) {
        this.apiCallerFactory = apiCallerFactory;
    }

    /**
     * Use this method to instantiate the {@link ApiCaller} so the @Value private fields can be set before they are required at the ApiCaller instantiation.
     */
    protected ApiCaller getApiCaller() {
        if (apiCaller == null) {
            apiCaller = apiCallerFactory.create(getApiBaseUrl());
        }
        return apiCaller;
    }

    protected abstract String getApiBaseUrl();
}
