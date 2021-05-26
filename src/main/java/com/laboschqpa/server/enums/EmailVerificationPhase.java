package com.laboschqpa.server.enums;

import com.laboschqpa.server.exceptions.NotImplementedException;

import java.util.Arrays;
import java.util.Optional;

public enum EmailVerificationPhase {
    EMAIL_SUBMITTED(1),
    EMAIL_VERIFIED(2);
    private Integer value;

    EmailVerificationPhase(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public static EmailVerificationPhase fromValue(Integer value) {
        Optional<EmailVerificationPhase> optional = Arrays.stream(EmailVerificationPhase.values())
                .filter(en -> en.getValue().equals(value))
                .findFirst();

        if (optional.isEmpty())
            throw new NotImplementedException("Enum from this value is not implemented" + value);

        return optional.get();
    }
}
