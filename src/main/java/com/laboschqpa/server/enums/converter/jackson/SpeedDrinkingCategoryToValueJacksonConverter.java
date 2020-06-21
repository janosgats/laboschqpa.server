package com.laboschqpa.server.enums.converter.jackson;

import com.fasterxml.jackson.databind.util.StdConverter;
import com.laboschqpa.server.enums.ugc.SpeedDrinkingCategory;

public class SpeedDrinkingCategoryToValueJacksonConverter extends StdConverter<SpeedDrinkingCategory, Integer> {
    @Override
    public Integer convert(SpeedDrinkingCategory source) {
        return source.getValue();
    }
}