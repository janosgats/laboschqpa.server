package com.laboschqpa.server.service.apiclient.filehost;

import com.laboschqpa.server.service.apiclient.AbstractApiClient;
import com.laboschqpa.server.service.apiclient.ApiCallerFactory;
import com.laboschqpa.server.service.apiclient.filehost.dto.GetIndexedFileInfoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.Map;
import java.util.Set;

@Service
public class FileHostApiClient extends AbstractApiClient {
    @Value("${apiClient.fileHost.baseUrl}")
    private String apiBaseUrl;
    @Value("${apiClient.fileHost.indexedFileInfo.url}")
    private String indexedFileInfoUrl;
    @Value("${apiClient.fileHost.listSucceededImageVariantIdsOfFile.url}")
    private String listSucceededImageVariantIdsOfFileUrl;
    @Value("${apiClient.fileHost.markImageVariantFileAsCorrupt.url}")
    private String markImageVariantFileAsCorruptUrl;

    @Value("${apiClient.fileHost.deleteFile.url}")
    private String deleteFileUrl;

    public FileHostApiClient(ApiCallerFactory apiCallerFactory) {
        super(apiCallerFactory, true);
    }

    public GetIndexedFileInfoResponse[] getIndexedFileInfo(Set<Long> indexedFileIds) {
        return getApiCaller().doCallAndThrowExceptionIfStatuscodeIsNot2xx(GetIndexedFileInfoResponse[].class,
                indexedFileInfoUrl,
                HttpMethod.GET,
                BodyInserters.fromValue(indexedFileIds));
    }

    public Long[] listSucceededImageVariantIdsOfFile(long originalFileId) {
        return getApiCaller().doCallAndThrowExceptionIfStatuscodeIsNot2xx(Long[].class,
                listSucceededImageVariantIdsOfFileUrl,
                HttpMethod.GET,
                Map.of("originalFileId", String.valueOf(originalFileId))
        );
    }

    public void markImageVariantFileAsCorruptUrl(long variantFileId) {
        getApiCaller().doCallAndThrowExceptionIfStatuscodeIsNot2xx(Long[].class,
                markImageVariantFileAsCorruptUrl,
                HttpMethod.POST,
                Map.of("variantFileId", String.valueOf(variantFileId))
        );
    }

    public void deleteFile(long indexedFileId) {
        getApiCaller().doCallAndThrowExceptionIfStatuscodeIsNot2xx(String.class,
                deleteFileUrl,
                HttpMethod.DELETE,
                Map.of("id", String.valueOf(indexedFileId)));
    }

    @Override
    protected String getApiBaseUrl() {
        return apiBaseUrl;
    }
}
