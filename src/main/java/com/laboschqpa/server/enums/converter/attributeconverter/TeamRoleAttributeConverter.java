package com.laboschqpa.server.enums.converter.attributeconverter;

import com.laboschqpa.server.enums.auth.TeamRole;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class TeamRoleAttributeConverter implements AttributeConverter<TeamRole, Integer> {
    @Override
    public Integer convertToDatabaseColumn(TeamRole enumVal) {
        return enumVal.getValue();
    }

    @Override
    public TeamRole convertToEntityAttribute(Integer val) {
        return TeamRole.fromValue(val);
    }
}
