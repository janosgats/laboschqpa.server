package com.laboschqpa.server.enums.attributeconverter;

import com.laboschqpa.server.enums.filehost.StoredFileStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class StoredFileStatusAttributeConverter implements AttributeConverter<StoredFileStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(StoredFileStatus enumVal) {
        return enumVal.getValue();
    }

    @Override
    public StoredFileStatus convertToEntityAttribute(Integer val) {
        return StoredFileStatus.fromValue(val);
    }
}
