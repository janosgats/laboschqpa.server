package com.laboschqpa.server.repo.usergeneratedcontent.dto;

import com.laboschqpa.server.entity.usergeneratedcontent.Objective;
import com.laboschqpa.server.enums.ugc.ObjectiveType;

import java.time.Instant;

public class ObjectiveDtoAdapter extends UserGeneratedContentDtoAdapter implements GetObjectiveJpaDto {

    private final Objective delegate;

    public ObjectiveDtoAdapter(Objective delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    @Override
    public Long getProgramId() {
        return delegate.getProgram().getId();
    }

    @Override
    public String getProgramTitle() {
        return delegate.getProgram().getTitle();
    }

    @Override
    public String getTitle() {
        return delegate.getTitle();
    }

    @Override
    public String getDescription() {
        return delegate.getDescription();
    }

    @Override
    public Boolean getSubmittable() {
        return delegate.getSubmittable();
    }

    @Override
    public Instant getDeadline() {
        return delegate.getDeadline();
    }

    @Override
    public Instant getHideSubmissionsBefore() {
        return delegate.getHideSubmissionsBefore();
    }

    @Override
    public ObjectiveType getObjectiveType() {
        return delegate.getObjectiveType();
    }

    @Override
    public Boolean getIsHidden() {
        return delegate.getIsHidden();
    }
}
