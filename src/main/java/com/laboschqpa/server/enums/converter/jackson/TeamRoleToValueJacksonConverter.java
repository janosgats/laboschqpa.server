package com.laboschqpa.server.enums.converter.jackson;

import com.fasterxml.jackson.databind.util.StdConverter;
import com.laboschqpa.server.enums.auth.TeamRole;

public class TeamRoleToValueJacksonConverter extends StdConverter<TeamRole, Integer> {
    @Override
    public Integer convert(TeamRole source) {
        return source.getValue();
    }
}