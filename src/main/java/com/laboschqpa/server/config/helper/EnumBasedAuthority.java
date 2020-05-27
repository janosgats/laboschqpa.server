package com.laboschqpa.server.config.helper;

import com.laboschqpa.server.enums.auth.Authority;
import org.springframework.security.core.GrantedAuthority;

public class EnumBasedAuthority implements GrantedAuthority {
    private static final long serialVersionUID = 1L;

    private Authority authority;

    public EnumBasedAuthority(Authority authority) {
        this.authority = authority;
    }

    public EnumBasedAuthority(String authorityStringValue) {
        this.authority = Authority.fromStringValue(authorityStringValue);
    }

    @Override
    public String getAuthority() {
        return authority.getStringValue();
    }
}
