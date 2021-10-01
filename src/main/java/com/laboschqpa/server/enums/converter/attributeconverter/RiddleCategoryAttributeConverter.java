package com.laboschqpa.server.enums.converter.attributeconverter;

import com.laboschqpa.server.enums.RiddleCategory;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class RiddleCategoryAttributeConverter implements AttributeConverter<RiddleCategory, Integer> {
    @Override
    public Integer convertToDatabaseColumn(RiddleCategory enumVal) {
        return enumVal.getValue();
    }

    @Override
    public RiddleCategory convertToEntityAttribute(Integer val) {
        return RiddleCategory.fromValue(val);
    }
}
