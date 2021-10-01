package com.laboschqpa.server.enums;

import com.laboschqpa.server.exceptions.NotImplementedException;

import java.util.Arrays;
import java.util.Optional;

public enum RiddleCategory {
    BEST(1),
    EVEN_BETTER(2);

    private Integer value;

    RiddleCategory(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }


    public static RiddleCategory fromValue(Integer value) {
        Optional<RiddleCategory> optional = Arrays.stream(RiddleCategory.values())
                .filter(en -> en.getValue().equals(value))
                .findFirst();

        if (optional.isEmpty())
            throw new NotImplementedException("Enum from this value is not implemented" + value);

        return optional.get();
    }
}
