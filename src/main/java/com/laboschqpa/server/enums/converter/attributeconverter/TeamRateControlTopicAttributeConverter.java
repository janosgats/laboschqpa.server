package com.laboschqpa.server.enums.converter.attributeconverter;

import com.laboschqpa.server.enums.TeamRateControlTopic;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class TeamRateControlTopicAttributeConverter implements AttributeConverter<TeamRateControlTopic, Integer> {
    @Override
    public Integer convertToDatabaseColumn(TeamRateControlTopic enumVal) {
        return enumVal.getValue();
    }

    @Override
    public TeamRateControlTopic convertToEntityAttribute(Integer val) {
        return TeamRateControlTopic.fromValue(val);
    }
}
