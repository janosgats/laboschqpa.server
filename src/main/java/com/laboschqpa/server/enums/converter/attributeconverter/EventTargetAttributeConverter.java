package com.laboschqpa.server.enums.converter.attributeconverter;

import com.laboschqpa.server.enums.event.EventTarget;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class EventTargetAttributeConverter implements AttributeConverter<EventTarget, Integer> {
    @Override
    public Integer convertToDatabaseColumn(EventTarget enumVal) {
        return enumVal.getValue();
    }

    @Override
    public EventTarget convertToEntityAttribute(Integer val) {
        return EventTarget.fromValue(val);
    }
}
