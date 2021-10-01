package com.laboschqpa.server.enums.converter.jackson;

import com.fasterxml.jackson.databind.util.StdConverter;
import com.laboschqpa.server.enums.RiddleCategory;

public class RiddleCategoryFromValueJacksonConverter extends StdConverter<Integer, RiddleCategory> {
    @Override
    public RiddleCategory convert(Integer source) {
        return RiddleCategory.fromValue(source);
    }
}