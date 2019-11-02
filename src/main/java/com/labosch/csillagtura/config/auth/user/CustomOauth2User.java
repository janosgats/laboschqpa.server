package com.labosch.csillagtura.config.auth.user;

import com.labosch.csillagtura.entity.User;
import com.labosch.csillagtura.exceptions.NotImplementedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CustomOauth2User implements OidcUser, OAuth2User, Serializable {
    static final long serialVersionUID = 42L;

    private ArrayList<GrantedAuthority> authorities;
    private String name;
    private Map<String, Object> attributes;

    private User userEntity;

    public User getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(User userEntity) {
        this.userEntity = userEntity;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return name;
    }
    public void setAuthorities(ArrayList<GrantedAuthority> grantedAuthorities) {
        this.authorities = grantedAuthorities;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }



    @Override
    public Map<String, Object> getClaims() {
        throw new NotImplementedException("OIDC part of CustomOauth2User is not implemented.");
    }

    @Override
    public OidcUserInfo getUserInfo() {
        throw new NotImplementedException("OIDC part of CustomOauth2User is not implemented.");
    }

    @Override
    public OidcIdToken getIdToken() {
        throw new NotImplementedException("OIDC part of CustomOauth2User is not implemented.");
    }
}
