package com.laboschqpa.server.enums.auth;

import com.laboschqpa.server.exceptions.NotImplementedException;

import java.util.Arrays;
import java.util.Optional;

public enum TeamRole {
    NOTHING(0),
    APPLIED(1),
    MEMBER(2),
    LEADER(3);

    private Integer value;

    TeamRole(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public boolean isMemberOrLeader() {
        return this == MEMBER || this == LEADER;
    }

    public static TeamRole fromValue(Integer value) {
        Optional<TeamRole> optional = Arrays.stream(TeamRole.values())
                .filter(en -> en.getValue().equals(value))
                .findFirst();

        if (optional.isEmpty())
            throw new NotImplementedException("Enum from this value is not implemented" + value);

        return optional.get();
    }
}
