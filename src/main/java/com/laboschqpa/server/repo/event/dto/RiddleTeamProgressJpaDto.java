package com.laboschqpa.server.repo.event.dto;

public interface RiddleTeamProgressJpaDto extends EventForUserJpaDto {
    Long getTeamId();

    String getTeamName();

    Integer getSolvedRiddleCount();
}
