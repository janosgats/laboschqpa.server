package com.laboschqpa.server.enums.auth;

import com.laboschqpa.server.exceptions.NotImplementedException;

import java.util.Arrays;
import java.util.Optional;

public enum Authority {
    User("User"),
    NewsPostEditor("NewsPostEditor"),
    ProgramEditor("ProgramEditor"),
    ObjectiveEditor("ObjectiveEditor"),
    RiddleEditor("RiddleEditor"),
    EventEditor("EventEditor"),
    TeamScorer("TeamScorer"),
    SpeedDrinkingEditor("SpeedDrinkingEditor"),
    AcceptedEmailEditor("AcceptedEmailEditor"),
    FileSupervisor("FileSupervisor"),
    Admin("Admin");

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
