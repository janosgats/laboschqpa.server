package com.laboschqpa.server.service.oauth2;

import com.laboschqpa.server.enums.auth.OAuth2ProviderRegistration;
import com.laboschqpa.server.exceptions.NotImplementedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class OAuth2ProviderFactory {
    private final GoogleOAuth2Provider googleOAuth2ProviderService;
    private final GithubOAuth2Provider githubOAuth2ProviderService;

    public OAuth2Provider get(OAuth2ProviderRegistration providerRegistration) {
        switch (providerRegistration) {
            case Google:
                return googleOAuth2ProviderService;
            case GitHub:
                return githubOAuth2ProviderService;
            default:
                throw new NotImplementedException("OAuth2 Provider Service is not implemented for " + providerRegistration);
        }
    }
}
