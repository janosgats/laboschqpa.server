package com.labosch.csillagtura.server.config.auth.authorities;

import org.springframework.security.core.GrantedAuthority;

public class EnumBasedAuthority implements GrantedAuthority {
    private Authority authority;

    public EnumBasedAuthority(Authority authority) {
        this.authority = authority;
    }

    public EnumBasedAuthority(String authorityStringValue) {
        this.authority = Authority.valueOf(authorityStringValue);
    }

    @Override
    public String getAuthority() {
        return authority.toString();
    }
}
