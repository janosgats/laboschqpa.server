package com.laboschqpa.server.repo.usergeneratedcontent.dto;

import java.time.Instant;

public interface GetRiddleFirstSolutionJpaDto {
    Long getTeamId();

    String getTeamName();

    Instant getSolvingTimestamp();
}
