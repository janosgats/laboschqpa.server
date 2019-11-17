package com.laboschcst.server.config.auth.user;

import com.laboschcst.server.entity.UserAcc;
import com.laboschcst.server.exceptions.NotImplementedException;
import com.laboschcst.server.repo.UserAccRepository;
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

    private transient UserAcc userAccEntity;
    private Long userId;

    public UserAcc getUserAccEntity() {
        return userAccEntity;
    }

    public void refreshUserEntityIfNull_FromDB(UserAccRepository userAccRepository) {
        if (userId == null)
            userAccEntity = null;
        else
            userAccEntity = userAccRepository.findById(userId).orElse(null);
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUserIdAndLoadFromDb(Long userId, UserAccRepository userAccRepository) {
        this.userId = userId;
        refreshUserEntityIfNull_FromDB(userAccRepository);
        //TODO: Setting authorities of SecurityContext by the authorities of the userEntity
    }

    public void setUserAccEntity(UserAcc userAccEntity) {
        if (userAccEntity == null) {
            this.userId = null;
            this.userAccEntity = null;
        } else {
            this.userId = userAccEntity.getId();
            this.userAccEntity = userAccEntity;
        }
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

    public Long getUserId() {
        return userId;
    }
}
