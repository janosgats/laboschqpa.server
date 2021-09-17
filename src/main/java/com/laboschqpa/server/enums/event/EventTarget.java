package com.laboschqpa.server.enums.event;

import com.fasterxml.jackson.annotation.JsonValue;
import com.laboschqpa.server.exceptions.NotImplementedException;

import java.util.Arrays;
import java.util.Optional;

public enum EventTarget {
    PERSONAL(Integer.parseInt(EventTargetTypeValues.PERSONAL)),
    TEAM(Integer.parseInt(EventTargetTypeValues.TEAM));

    private Integer value;

    EventTarget(Integer value) {
        this.value = value;
    }

    @JsonValue
    public Integer getValue() {
        return value;
    }

    public static EventTarget fromValue(Integer value) {
        Optional<EventTarget> optional = Arrays.stream(EventTarget.values())
                .filter(en -> en.getValue().equals(value))
                .findFirst();

        if (optional.isEmpty())
            throw new NotImplementedException("Enum from this value is not implemented" + value);

        return optional.get();
    }
}
