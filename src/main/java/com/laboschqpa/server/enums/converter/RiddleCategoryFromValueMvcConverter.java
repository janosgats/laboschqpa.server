package com.laboschqpa.server.enums.converter;

import com.laboschqpa.server.enums.RiddleCategory;
import org.springframework.core.convert.converter.Converter;

public class RiddleCategoryFromValueMvcConverter implements Converter<String, RiddleCategory> {
    @Override
    public RiddleCategory convert(String source) {
        return RiddleCategory.fromValue(Integer.parseInt(source));
    }
}