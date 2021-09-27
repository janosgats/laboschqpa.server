package com.laboschqpa.server.repo.usergeneratedcontent.dto;

import java.sql.Timestamp;
import java.time.Instant;

public interface GetRiddleFirstSolutionJpaDto {
    Long getTeamId();

    String getTeamName();

    Timestamp getSolvingTime();

    default Instant getSolvingTimeAsInstant() {
        if (getSolvingTime() == null) {
            return null;
        }
        return getSolvingTime().toInstant();
    }
}
