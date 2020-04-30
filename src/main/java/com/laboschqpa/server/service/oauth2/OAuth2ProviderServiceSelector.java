package com.laboschqpa.server.service.oauth2;

import com.laboschqpa.server.enums.auth.OAuth2ProviderRegistrations;
import com.laboschqpa.server.exceptions.NotImplementedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class OAuth2ProviderServiceSelector {
    private final GoogleOAuth2ProviderService googleOAuth2ProviderService;
    private final GithubOAuth2ProviderService githubOAuth2ProviderService;

    public AbstractOAuth2ProviderService getProviderService(OAuth2ProviderRegistrations providerRegistration) {
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
