package com.laboschqpa.server.enums.ugc;

import com.laboschqpa.server.exceptions.NotImplementedException;

import java.util.Arrays;
import java.util.Optional;

public enum UserGeneratedContentType {
    NEWS_POST(Integer.parseInt(UserGeneratedContentTypeValues.NEWS_POST)),
    OBJECTIVE(Integer.parseInt(UserGeneratedContentTypeValues.OBJECTIVE)),
    SUBMISSION(Integer.parseInt(UserGeneratedContentTypeValues.SUBMISSION)),
    RIDDLE(Integer.parseInt(UserGeneratedContentTypeValues.RIDDLE));

    private Integer value;

    UserGeneratedContentType(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public static UserGeneratedContentType fromValue(Integer value) {
        Optional<UserGeneratedContentType> optional = Arrays.stream(UserGeneratedContentType.values())
                .filter(en -> en.getValue().equals(value))
                .findFirst();

        if (optional.isEmpty())
            throw new NotImplementedException("Enum from this value is not implemented" + value);

        return optional.get();
    }
}
