package com.laboschqpa.server.enums.attributeconverter;

import com.laboschqpa.server.enums.riddle.RiddleResolutionStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class RiddleResolutionStatusAttributeConverter implements AttributeConverter<RiddleResolutionStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(RiddleResolutionStatus enumVal) {
        return enumVal.getValue();
    }

    @Override
    public RiddleResolutionStatus convertToEntityAttribute(Integer val) {
        return RiddleResolutionStatus.fromValue(val);
    }
}
