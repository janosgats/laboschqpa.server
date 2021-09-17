package com.laboschqpa.server.api.dto.event;

import com.laboschqpa.server.enums.event.EventTarget;
import com.laboschqpa.server.repo.event.dto.EventForUserJpaDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class EventForUserResponse {
    private Long id;
    private String name;
    private EventTarget target;
    private Integer registrationLimit;
    private Instant registrationDeadline;

    public EventForUserResponse(EventForUserJpaDto jpaDto) {
        this.id = jpaDto.getId();
        this.name = jpaDto.getName();
        this.target = jpaDto.getTarget();
        this.registrationLimit = jpaDto.getRegistrationLimit();
        this.registrationDeadline = jpaDto.getRegistrationDeadline();
    }
}
