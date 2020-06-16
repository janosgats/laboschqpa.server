package com.laboschqpa.server.enums.converter.jackson;

import com.fasterxml.jackson.databind.util.StdConverter;
import com.laboschqpa.server.enums.ugc.ObjectiveType;

public class ObjectiveTypeToValueJacksonConverter extends StdConverter<ObjectiveType, Integer> {
    @Override
    public Integer convert(ObjectiveType source) {
        return source.getValue();
    }
}