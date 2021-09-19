package com.laboschqpa.server.repo.usergeneratedcontent.dto;

import java.sql.Timestamp;
import java.time.Instant;

public interface GetProgramJpaDto extends GetUserGeneratedContentJpaDto {
    String getTitle();

    String getHeadline();

    String getDescription();

    Timestamp getStartTime();

    Timestamp getEndTime();

    default Instant getStartTimeAsInstant() {
        if (getStartTime() == null) {
            return null;
        }
        return getStartTime().toInstant();
    }

    default Instant getEndTimeAsInstant() {
        if (getEndTime() == null) {
            return null;
        }
        return getEndTime().toInstant();
    }
}
