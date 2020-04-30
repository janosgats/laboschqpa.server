package com.laboschqpa.server.config.authprovider;

import com.laboschqpa.server.enums.auth.OAuth2ProviderRegistrations;
import com.laboschqpa.server.exceptions.ConfigException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OAuth2ProviderRegistrationFactory {
    private final Environment env;

    public ClientRegistration createProviderRegistration(OAuth2ProviderRegistrations providerRegistration) {
        String clientId = getClientId(providerRegistration);
        String clientSecret = getClientSecret(providerRegistration);

        switch (providerRegistration) {
            case Google:
                return CommonOAuth2Provider.GOOGLE.getBuilder(OAuth2ProviderRegistrations.Google.getProviderRegistrationKey())
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .build();
            case GitHub:
                return CommonOAuth2Provider.GITHUB.getBuilder(OAuth2ProviderRegistrations.GitHub.getProviderRegistrationKey())
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .build();
            case Facebook:
                return CommonOAuth2Provider.FACEBOOK.getBuilder(OAuth2ProviderRegistrations.Facebook.getProviderRegistrationKey())
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .build();
            default:
                throw new ConfigException("Unimplemented OAuth2 providerRegistration: " + providerRegistration);
        }
    }

    private String getClientId(OAuth2ProviderRegistrations provider) {
        String clientId = env.getProperty("oauth2.provider." + provider.getProviderRegistrationKey() + ".client.client-id");
        if (clientId == null) {
            throw new ConfigException("Missing clientId for OAuth2 provider: " + provider);
        }
        return clientId;
    }

    private String getClientSecret(OAuth2ProviderRegistrations provider) {
        String clientSecret = env.getProperty("oauth2.provider." + provider.getProviderRegistrationKey() + ".client.client-secret");
        if (clientSecret == null) {
            throw new ConfigException("Missing clientSecret for OAuth2 provider: " + provider);
        }
        return clientSecret;
    }
}
