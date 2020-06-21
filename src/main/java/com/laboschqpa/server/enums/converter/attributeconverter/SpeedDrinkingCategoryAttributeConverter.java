package com.laboschqpa.server.enums.converter.attributeconverter;

import com.laboschqpa.server.enums.ugc.SpeedDrinkingCategory;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class SpeedDrinkingCategoryAttributeConverter implements AttributeConverter<SpeedDrinkingCategory, Integer> {
    @Override
    public Integer convertToDatabaseColumn(SpeedDrinkingCategory enumVal) {
        return enumVal.getValue();
    }

    @Override
    public SpeedDrinkingCategory convertToEntityAttribute(Integer val) {
        return SpeedDrinkingCategory.fromValue(val);
    }
}
