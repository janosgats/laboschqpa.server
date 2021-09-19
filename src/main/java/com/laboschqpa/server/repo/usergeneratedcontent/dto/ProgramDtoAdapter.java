package com.laboschqpa.server.repo.usergeneratedcontent.dto;

import com.laboschqpa.server.entity.usergeneratedcontent.Program;

import java.sql.Timestamp;
import java.time.Instant;

public class ProgramDtoAdapter extends UserGeneratedContentDtoAdapter implements GetProgramJpaDto {

    private final Program delegate;

    public ProgramDtoAdapter(Program delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    @Override
    public String getTitle() {
        return delegate.getTitle();
    }

    @Override
    public String getHeadline() {
        return delegate.getHeadline();
    }

    @Override
    public String getDescription() {
        return delegate.getDescription();
    }

    @Override
    public Timestamp getStartTime() {
        return Timestamp.from(delegate.getStartTime());
    }

    @Override
    public Timestamp getEndTime() {
        return Timestamp.from(delegate.getEndTime());
    }

    @Override
    public Instant getCreationTimeAsInstant() {
        return delegate.getStartTime();
    }

    @Override
    public Instant getEditTimeAsInstant() {
        return delegate.getEndTime();
    }
}
