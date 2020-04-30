package com.laboschqpa.server.config.userservice;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {
    private final UserAccountLoginManager userAccountLoginManager;

    @Override
    public OidcUser loadUser(OidcUserRequest oidcUserRequest) {
        return userAccountLoginManager.getExactUser(oidcUserRequest);
    }
}
