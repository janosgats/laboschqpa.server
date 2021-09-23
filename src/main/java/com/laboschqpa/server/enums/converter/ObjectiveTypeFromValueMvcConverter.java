package com.laboschqpa.server.enums.converter;

import com.laboschqpa.server.enums.ugc.ObjectiveType;
import org.springframework.core.convert.converter.Converter;

public class ObjectiveTypeFromValueMvcConverter implements Converter<String, ObjectiveType> {
    @Override
    public ObjectiveType convert(String source) {
        return ObjectiveType.fromValue(Integer.parseInt(source));
    }
}