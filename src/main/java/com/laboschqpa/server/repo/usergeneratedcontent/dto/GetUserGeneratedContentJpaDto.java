package com.laboschqpa.server.repo.usergeneratedcontent.dto;

import java.time.Instant;

public interface GetUserGeneratedContentJpaDto {
    Long getId();

    Long getCreatorUserId();

    Long getEditorUserId();

    Instant getCreationTime();

    Instant getEditTime();
}
