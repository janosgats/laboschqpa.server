package com.laboschqpa.server.repo.usergeneratedcontent.dto;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Set;

public interface GetUserGeneratedContentJpaDto {
    Long getId();

    Long getCreatorUserId();

    Long getEditorUserId();

    Timestamp getCreationTime();

    Timestamp getEditTime();

    Set<Long> getAttachments();

    default Instant getCreationTimeAsInstant() {
        if (getCreationTime() == null) {
            return null;
        }
        return getCreationTime().toInstant();
    }

    default Instant getEditTimeAsInstant() {
        if (getEditTime() == null) {
            return null;
        }
        return getEditTime().toInstant();
    }
}
