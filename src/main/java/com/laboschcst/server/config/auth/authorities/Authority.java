package com.laboschcst.server.config.auth.authorities;

import com.laboschcst.server.exceptions.NotImplementedException;

public enum Authority {
    User("user"),
    Admin("admin");

    private String stringValue;

    Authority(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public static Authority fromStringValue(String stringValue) {
        switch (stringValue) {
            case "user":
                return User;
            case "admin":
                return Admin;
        }

        throw new NotImplementedException("Enum from this string value is not implemented");
    }

    @Override
    public String toString() {
        return getStringValue();
    }
}
