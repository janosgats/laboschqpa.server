package com.laboschqpa.server.enums.attributeconverter;

import com.laboschqpa.server.enums.auth.Authority;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class AuthorityAttributeConverter implements AttributeConverter<Authority, String> {
    @Override
    public String convertToDatabaseColumn(Authority enumVal) {
        return enumVal.getStringValue();
    }

    @Override
    public Authority convertToEntityAttribute(String stringValue) {
        return Authority.fromStringValue(stringValue);
    }
}
