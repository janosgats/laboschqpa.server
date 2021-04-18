package com.laboschqpa.server.enums.ugc;

import com.laboschqpa.server.exceptions.NotImplementedException;

import java.util.Arrays;
import java.util.Optional;

public enum SpeedDrinkingCategory {
    BEER(1),
    RANDOM(2);

    private Integer value;

    SpeedDrinkingCategory(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public static SpeedDrinkingCategory fromValue(Integer value) {
        Optional<SpeedDrinkingCategory> optional = Arrays.stream(SpeedDrinkingCategory.values())
                .filter(en -> en.getValue().equals(value))
                .findFirst();

        if (optional.isEmpty())
            throw new NotImplementedException("Enum from this value is not implemented" + value);

        return optional.get();
    }
}
