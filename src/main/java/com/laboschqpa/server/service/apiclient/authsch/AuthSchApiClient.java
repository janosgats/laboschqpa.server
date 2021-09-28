package com.laboschqpa.server.service.apiclient.authsch;

import com.laboschqpa.server.service.apiclient.AbstractApiClient;
import com.laboschqpa.server.service.apiclient.ApiCallerFactory;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import java.util.Map;

@Log4j2
@Service
public class AuthSchApiClient extends AbstractApiClient {

    @Value("${apiClient.authsch.baseUrl}")
    private String apiBaseUrl;
    @Value("${apiClient.authsch.oauth2ResourceUserInfo.url}")
    private String oauth2ResourceUserInfoUrl;

    public AuthSchApiClient(ApiCallerFactory apiCallerFactory) {
        super(apiCallerFactory, false);
    }

    public AuthSchUserInfoDto getOAuth2UserInfo(OAuth2AccessToken accessToken) {
        return getApiCaller().doCallAndThrowExceptionIfStatuscodeIsNot2xx(AuthSchUserInfoDto.class,
                oauth2ResourceUserInfoUrl,
                HttpMethod.GET,
                Map.of("access_token", accessToken.getTokenValue())
        );
    }

    @Override
    protected String getApiBaseUrl() {
        return apiBaseUrl;
    }
}
