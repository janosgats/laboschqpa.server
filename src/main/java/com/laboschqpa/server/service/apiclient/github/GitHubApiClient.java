package com.laboschqpa.server.service.apiclient.github;

import com.laboschqpa.server.service.apiclient.AbstractApiClient;
import com.laboschqpa.server.service.apiclient.ApiCallerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

@Service
public class GitHubApiClient extends AbstractApiClient {

    @Value("${apiClient.github.baseUrl}")
    private String apiBaseUrl;
    @Value("${apiClient.github.oauth2ResourceUserInfo.url}")
    private String oauth2ResourceUserInfoUrl;

    public GitHubApiClient(ApiCallerFactory apiCallerFactory) {
        super(apiCallerFactory);
    }

    public GithubUserInfoDto getOAuth2UserInfo(OAuth2AccessToken accessToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", accessToken.getTokenType().getValue() + " " + accessToken.getTokenValue());

        return getApiCaller().doCallAndThrowExceptionIfStatuscodeIsNot2xx(GithubUserInfoDto.class,
                oauth2ResourceUserInfoUrl,
                HttpMethod.GET,
                httpHeaders);
    }

    @Override
    protected String getApiBaseUrl() {
        return apiBaseUrl;
    }
}
