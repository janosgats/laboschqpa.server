package com.laboschqpa.server.api.dto.event;

import com.laboschqpa.server.repo.event.dto.PersonalEventForUserJpaDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetPersonalEventForUserResponse extends EventForUserResponse {
    private Boolean isUserRegistered;

    public GetPersonalEventForUserResponse(PersonalEventForUserJpaDto jpaDto) {
        super(jpaDto);
        this.isUserRegistered = jpaDto.getPersonalEventRegistrationId() != null;
    }
}
