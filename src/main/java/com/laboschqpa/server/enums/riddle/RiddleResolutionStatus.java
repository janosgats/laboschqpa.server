package com.laboschqpa.server.enums.riddle;

import com.laboschqpa.server.exceptions.NotImplementedException;

import java.util.Arrays;
import java.util.Optional;

public enum RiddleResolutionStatus {
    UNSOLVED(Integer.parseInt(RiddleResolutionStatusValues.UNSOLVED)),
    SOLVED(Integer.parseInt(RiddleResolutionStatusValues.SOLVED));

    private Integer value;

    RiddleResolutionStatus(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public static RiddleResolutionStatus fromValue(Integer value) {
        Optional<RiddleResolutionStatus> optional = Arrays.stream(RiddleResolutionStatus.values())
                .filter(en -> en.getValue().equals(value))
                .findFirst();

        if (optional.isEmpty())
            throw new NotImplementedException("Enum from this value is not implemented" + value);

        return optional.get();
    }
}
