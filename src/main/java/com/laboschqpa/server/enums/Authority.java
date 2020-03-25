package com.laboschqpa.server.enums;

import com.laboschqpa.server.exceptions.NotImplementedException;

import java.util.Arrays;
import java.util.Optional;

public enum Authority {
    User("user"),
    Editor("editor"),
    Admin("admin");

    private String stringValue;

    Authority(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public static Authority fromStringValue(String stringValue) {
        Optional<Authority> authorityOptional = Arrays.stream(Authority.values())
                .filter(en -> en.getStringValue().equals(stringValue))
                .findFirst();

        if (authorityOptional.isEmpty())
            throw new NotImplementedException("Enum from this string value is not implemented");

        return authorityOptional.get();
    }

    @Override
    public String toString() {
        return getStringValue();
    }
}
