package com.laboschqpa.server.enums.converter.jackson;

import com.fasterxml.jackson.databind.util.StdConverter;
import com.laboschqpa.server.enums.ugc.ObjectiveType;

public class ObjectiveTypeFromValueJacksonConverter extends StdConverter<Integer, ObjectiveType> {
    @Override
    public ObjectiveType convert(Integer source) {
        return ObjectiveType.fromValue(source);
    }
}