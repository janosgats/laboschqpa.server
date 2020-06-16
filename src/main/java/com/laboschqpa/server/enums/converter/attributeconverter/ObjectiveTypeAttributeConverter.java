package com.laboschqpa.server.enums.converter.attributeconverter;

import com.laboschqpa.server.enums.ugc.ObjectiveType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ObjectiveTypeAttributeConverter implements AttributeConverter<ObjectiveType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(ObjectiveType enumVal) {
        return enumVal.getValue();
    }

    @Override
    public ObjectiveType convertToEntityAttribute(Integer val) {
        return ObjectiveType.fromValue(val);
    }
}
