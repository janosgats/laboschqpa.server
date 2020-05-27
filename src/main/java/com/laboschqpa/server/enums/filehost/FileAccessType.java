package com.laboschqpa.server.enums.filehost;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.laboschqpa.server.exceptions.NotImplementedException;

import java.util.Arrays;
import java.util.Optional;

public enum
FileAccessType {
    READ(0),
    DELETE(1),
    WRITE(2);

    private Integer value;

    FileAccessType(Integer value) {
        this.value = value;
    }

    @JsonValue
    public Integer getValue() {
        return value;
    }

    @JsonCreator
    public static FileAccessType fromValue(Integer value) {
        Optional<FileAccessType> optional = Arrays.stream(FileAccessType.values())
                .filter(en -> en.getValue().equals(value))
                .findFirst();

        if (optional.isEmpty())
            throw new NotImplementedException("Enum from this value is not implemented" + value);

        return optional.get();
    }
}
