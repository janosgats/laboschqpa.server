package com.laboschqpa.server.enums.attributeconverter;

import com.laboschqpa.server.enums.RegistrationRequestPhase;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class RegistrationRequestPhaseAttributeConverter implements AttributeConverter<RegistrationRequestPhase, Integer> {
    @Override
    public Integer convertToDatabaseColumn(RegistrationRequestPhase enumVal) {
        return enumVal.getValue();
    }

    @Override
    public RegistrationRequestPhase convertToEntityAttribute(Integer val) {
        return RegistrationRequestPhase.fromValue(val);
    }
}
