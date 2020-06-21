package com.laboschqpa.server.enums.converter.jackson;

import com.fasterxml.jackson.databind.util.StdConverter;
import com.laboschqpa.server.enums.ugc.SpeedDrinkingCategory;

public class SpeedDrinkingCategoryFromValueJacksonConverter extends StdConverter<Integer, SpeedDrinkingCategory> {
    @Override
    public SpeedDrinkingCategory convert(Integer source) {
        return SpeedDrinkingCategory.fromValue(source);
    }
}