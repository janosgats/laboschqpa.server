package com.laboschqpa.server.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.laboschqpa.server.exceptions.NotImplementedException;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

public enum TeamRateControlTopic {
    QR_FIGHT_TAG_SUBMISSION_TRIAL(1, "qrFightTagSubmission"),
    RIDDLE_SUBMISSION_TRIAL(2, "riddleSubmission");

    private Integer value;
    @Getter
    private String prometheusKey;

    TeamRateControlTopic(Integer value, String prometheusKey) {
        this.value = value;
        this.prometheusKey = prometheusKey;
    }

    @JsonValue
    public Integer getValue() {
        return value;
    }

    public static TeamRateControlTopic fromValue(Integer value) {
        Optional<TeamRateControlTopic> optional = Arrays.stream(values())
                .filter(en -> en.getValue().equals(value))
                .findFirst();

        if (optional.isEmpty())
            throw new NotImplementedException("Enum from this value is not implemented");

        return optional.get();
    }
}
