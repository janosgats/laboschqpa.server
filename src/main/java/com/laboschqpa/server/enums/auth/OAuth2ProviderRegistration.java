package com.laboschqpa.server.enums.auth;

import java.util.Arrays;
import java.util.Optional;

public enum OAuth2ProviderRegistration {
    Google("google"),
    GitHub("github"),
    Facebook("facebook"),
    AuthSch("authsch");

    private String providerRegistrationKey;

    OAuth2ProviderRegistration(String providerRegistrationKey) {
        this.providerRegistrationKey = providerRegistrationKey;
    }

    public String getProviderRegistrationKey() {
        return providerRegistrationKey;
    }

    public static OAuth2ProviderRegistration fromRegistrationKey(String fromProviderRegistrationKey) {
        Optional<OAuth2ProviderRegistration> authorityOptional = Arrays.stream(OAuth2ProviderRegistration.values())
                .filter(en -> en.getProviderRegistrationKey().equals(fromProviderRegistrationKey))
                .findFirst();

        return authorityOptional.orElse(null);
    }
}
