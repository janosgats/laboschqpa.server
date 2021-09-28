package com.laboschqpa.server.config.authprovider;

import com.laboschqpa.server.enums.auth.OAuth2ProviderRegistration;
import com.laboschqpa.server.exceptions.ConfigException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OAuth2ProviderRegistrationFactory {
    private final Environment env;

    public ClientRegistration createProviderRegistration(OAuth2ProviderRegistration providerRegistration) {
        String clientId = getClientId(providerRegistration);
        String clientSecret = getClientSecret(providerRegistration);

        switch (providerRegistration) {
            case Google:
                return CommonOAuth2Provider.GOOGLE.getBuilder(OAuth2ProviderRegistration.Google.getProviderRegistrationKey())
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .build();
            case GitHub:
                return CommonOAuth2Provider.GITHUB.getBuilder(OAuth2ProviderRegistration.GitHub.getProviderRegistrationKey())
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .build();
            case Facebook:
                return CommonOAuth2Provider.FACEBOOK.getBuilder(OAuth2ProviderRegistration.Facebook.getProviderRegistrationKey())
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .build();
            case AuthSch:
                return getCommonAuthSchClientRegistrationBuilder()
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .build();

            default:
                throw new ConfigException("Unimplemented OAuth2 providerRegistration: " + providerRegistration);
        }
    }

    private ClientRegistration.Builder getCommonAuthSchClientRegistrationBuilder() {
        final String providerRegKey = OAuth2ProviderRegistration.AuthSch.getProviderRegistrationKey();

        final ClientRegistration.Builder builder = ClientRegistration.withRegistrationId(providerRegKey);
        builder
                .clientAuthenticationMethod(ClientAuthenticationMethod.BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost:8080/login/oauth2/code/" + providerRegKey)
                .scope("basic", "sn", "givenName", "mail", "linkedAccounts")
                .authorizationUri("https://auth.sch.bme.hu/site/login")
                .tokenUri("https://auth.sch.bme.hu/oauth2/token")
                .userInfoUri("https://auth.sch.bme.hu/api/profile")
                .userInfoAuthenticationMethod(AuthenticationMethod.QUERY)
                .clientName("AuthSch");

        return builder;
    }

    private String getClientId(OAuth2ProviderRegistration provider) {
        String clientId = env.getProperty("oauth2.provider." + provider.getProviderRegistrationKey() + ".client.client-id");
        if (clientId == null) {
            throw new ConfigException("Missing clientId for OAuth2 provider: " + provider);
        }
        return clientId;
    }

    private String getClientSecret(OAuth2ProviderRegistration provider) {
        String clientSecret = env.getProperty("oauth2.provider." + provider.getProviderRegistrationKey() + ".client.client-secret");
        if (clientSecret == null) {
            throw new ConfigException("Missing clientSecret for OAuth2 provider: " + provider);
        }
        return clientSecret;
    }
}
