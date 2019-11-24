package com.laboschcst.server.config.auth.authorities;

import com.laboschcst.server.exceptions.NotImplementedException;

public enum Authority {
    Test1("test1"),
    Test2("test2"),
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
            case "test1":
                return Test1;
            case "test2":
                return Test2;
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
