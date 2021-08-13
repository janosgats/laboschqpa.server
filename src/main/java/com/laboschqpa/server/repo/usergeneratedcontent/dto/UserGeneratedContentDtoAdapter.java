package com.laboschqpa.server.repo.usergeneratedcontent.dto;

import com.laboschqpa.server.entity.usergeneratedcontent.UserGeneratedContent;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Set;

public class UserGeneratedContentDtoAdapter implements GetUserGeneratedContentJpaDto {

    private final UserGeneratedContent delegate;

    public UserGeneratedContentDtoAdapter(UserGeneratedContent delegate) {
        this.delegate = delegate;
    }

    public Long getId() {
        return delegate.getId();
    }

    public Long getCreatorUserId() {
        return delegate.getCreatorUser().getId();
    }

    public Long getEditorUserId() {
        return delegate.getEditorUser().getId();
    }

    public Timestamp getCreationTime() {
        return Timestamp.from(delegate.getCreationTime());
    }

    public Timestamp getEditTime() {
        return Timestamp.from(delegate.getEditTime());
    }

    public Set<Long> getAttachments() {
        return delegate.getAttachments();
    }

    public Instant getCreationTimeAsInstant() {
        return delegate.getCreationTime();
    }

    public Instant getEditTimeAsInstant() {
        return delegate.getEditTime();
    }
}
