package com.laboschqpa.server.enums.auth;

import java.util.Arrays;
import java.util.Optional;

public enum OAuth2ProviderRegistrations {
    Google("google"),
    GitHub("github"),
    Facebook("facebook");

    private String providerRegistrationKey;

    OAuth2ProviderRegistrations(String providerRegistrationKey) {
        this.providerRegistrationKey = providerRegistrationKey;
    }

    public String getProviderRegistrationKey() {
        return providerRegistrationKey;
    }

    public static OAuth2ProviderRegistrations fromProviderRegistrationKey(String fromProviderRegistrationKey) {
        Optional<OAuth2ProviderRegistrations> authorityOptional = Arrays.stream(OAuth2ProviderRegistrations.values())
                .filter(en -> en.getProviderRegistrationKey().equals(fromProviderRegistrationKey))
                .findFirst();

        return authorityOptional.orElse(null);
    }
}
