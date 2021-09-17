package com.laboschqpa.server.repo.event.dto;

import com.laboschqpa.server.enums.event.EventTarget;

import java.time.Instant;


public interface EventForUserJpaDto {
    Long getId();

    String getName();

    EventTarget getTarget();

    Integer getRegistrationLimit();

    Instant getRegistrationDeadline();
}
