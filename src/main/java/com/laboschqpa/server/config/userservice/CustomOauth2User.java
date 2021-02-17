package com.laboschqpa.server.config.userservice;

import com.laboschqpa.server.config.helper.EnumBasedAuthority;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.exceptions.NotImplementedException;
import com.laboschqpa.server.repo.UserAccRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serializable;
import java.util.*;

@NoArgsConstructor
public class CustomOauth2User implements OidcUser, OAuth2User, Serializable {
    static final long serialVersionUID = 42L;
    private Map<String, Object> attributes;

    @Getter
    @Setter
    private transient boolean newlyRegistered = false;

    private transient UserAcc userAccEntity;
    private Long userId;

    public CustomOauth2User(UserAcc userAccEntity) {
        this.setUserAccEntity(userAccEntity);
    }

    public UserAcc getUserAccEntity() {
        return userAccEntity;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUserIdAndLoadFromDb(Long userId, UserAccRepository userAccRepository) {
        this.userId = userId;
        refreshUserAccEntityFromDB(userAccRepository);
    }

    public void refreshUserAccEntityFromDB(UserAccRepository userAccRepository) {
        if (userId == null)
            this.setUserAccEntity(null);
        else {
            Optional<UserAcc> userAccOptional = userAccRepository.findById(userId);
            if (userAccOptional.isEmpty())
                throw new RuntimeException("User account with Id " + userId + " not found in DB. Cannot refresh userAccEntity!");

            this.setUserAccEntity(userAccOptional.get());
        }
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
    public Set<? extends GrantedAuthority> getAuthorities() {
        if (userAccEntity != null)
            return userAccEntity.getCopyOfAuthorities_AsEnumBasedAuthority();
        else
            return new HashSet<>();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return userId != null ? userId.toString() : null;
    }

    public void setAuthorities(ArrayList<EnumBasedAuthority> grantedAuthorities) {

        if (userAccEntity != null)
            this.userAccEntity.setAuthorities_FromEnumBasedAuthority(grantedAuthorities);
        else
            throw new RuntimeException("userAccEntity is null!");
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
