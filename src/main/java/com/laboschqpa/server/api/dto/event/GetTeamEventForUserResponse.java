package com.laboschqpa.server.api.dto.event;

import com.laboschqpa.server.repo.event.dto.TeamEventForUserJpaDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetTeamEventForUserResponse extends EventForUserResponse {
    private Boolean isTeamRegistered;

    public GetTeamEventForUserResponse(TeamEventForUserJpaDto jpaDto) {
        super(jpaDto);
        this.isTeamRegistered = jpaDto.getTeamEventRegistrationId() != null;
    }
}
