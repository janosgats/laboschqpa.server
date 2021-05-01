package com.laboschqpa.server.service.apiclient.filehost;

import com.laboschqpa.server.service.apiclient.AbstractApiClient;
import com.laboschqpa.server.service.apiclient.ApiCallerFactory;
import com.laboschqpa.server.service.apiclient.filehost.dto.GetIndexedFileInfoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.Set;

@Service
public class FileHostApiClient extends AbstractApiClient {
    @Value("${apiClient.fileHost.baseUrl}")
    private String apiBaseUrl;
    @Value("${apiClient.fileHost.indexedFileInfo.url}")
    private String indexedFileInfoUrl;

    public FileHostApiClient(ApiCallerFactory apiCallerFactory) {
        super(apiCallerFactory, true);
    }

    public GetIndexedFileInfoResponse[] getIndexedFileInfo(Set<Long> indexedFileIds) {
        return getApiCaller().doCallAndThrowExceptionIfStatuscodeIsNot2xx(GetIndexedFileInfoResponse[].class,
                indexedFileInfoUrl,
                HttpMethod.GET,
                BodyInserters.fromValue(indexedFileIds));
    }

    @Override
    protected String getApiBaseUrl() {
        return apiBaseUrl;
    }
}
