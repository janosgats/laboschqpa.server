package com.laboschqpa.server.repo.dto;

import com.laboschqpa.server.enums.ugc.UserGeneratedContentType;

import java.time.Instant;

public interface UserGeneratedContentParentJpaDto {
    Long getId();

    Long getCreatorUserId();

    Long getEditorUserId();

    Instant getCreationTime();

    Instant getEditTime();

    Integer getDtype();

    default UserGeneratedContentType getType() {
        return UserGeneratedContentType.fromValue(getDtype());
    }
}
