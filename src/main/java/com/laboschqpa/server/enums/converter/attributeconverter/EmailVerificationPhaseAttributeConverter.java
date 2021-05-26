package com.laboschqpa.server.enums.converter.attributeconverter;

import com.laboschqpa.server.enums.EmailVerificationPhase;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class EmailVerificationPhaseAttributeConverter implements AttributeConverter<EmailVerificationPhase, Integer> {
    @Override
    public Integer convertToDatabaseColumn(EmailVerificationPhase enumVal) {
        return enumVal.getValue();
    }

    @Override
    public EmailVerificationPhase convertToEntityAttribute(Integer val) {
        return EmailVerificationPhase.fromValue(val);
    }
}
