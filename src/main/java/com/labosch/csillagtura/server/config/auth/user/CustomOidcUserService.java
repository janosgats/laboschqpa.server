package com.labosch.csillagtura.server.config.auth.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public class CustomOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {
    @Autowired
    ExactUserSelector exactUserSelector;

    @Override
    public OidcUser loadUser(OidcUserRequest oidcUserRequest) throws OAuth2AuthenticationException {
        return exactUserSelector.getExactUser(oidcUserRequest);
    }
}
