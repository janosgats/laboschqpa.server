package com.laboschqpa.server.repo.usergeneratedcontent.dto;

import java.sql.Timestamp;
import java.time.Instant;

public interface GetRiddleFirstSolutionJpaDto {
    Long getTeamId();

    String getTeamName();

    Timestamp getSolvingTimestamp();

    default Instant getSolvingTimestampAsInstant() {
        if (getSolvingTimestamp() == null) {
            return null;
        }
        return getSolvingTimestamp().toInstant();
    }
}
