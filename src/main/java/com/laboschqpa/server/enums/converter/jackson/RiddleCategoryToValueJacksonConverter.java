package com.laboschqpa.server.enums.converter.jackson;

import com.fasterxml.jackson.databind.util.StdConverter;
import com.laboschqpa.server.enums.RiddleCategory;

public class RiddleCategoryToValueJacksonConverter extends StdConverter<RiddleCategory, Integer> {
    @Override
    public Integer convert(RiddleCategory source) {
        return source.getValue();
    }
}