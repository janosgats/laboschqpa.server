package com.laboschqpa.server.enums;

import com.laboschqpa.server.exceptions.NotImplementedException;

import java.util.Arrays;
import java.util.Optional;

public enum RegistrationRequestPhase {
    EMAIL_SUBMITTED(1),
    EMAIL_VERIFIED(2),
    REGISTERED(3);
    private Integer value;

    RegistrationRequestPhase(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public static RegistrationRequestPhase fromValue(Integer value) {
        Optional<RegistrationRequestPhase> optional = Arrays.stream(RegistrationRequestPhase.values())
                .filter(en -> en.getValue().equals(value))
                .findFirst();

        if (optional.isEmpty())
            throw new NotImplementedException("Enum from this value is not implemented");

        return optional.get();
    }
}
