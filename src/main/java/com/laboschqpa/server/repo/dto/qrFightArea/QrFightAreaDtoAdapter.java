package com.laboschqpa.server.repo.dto.qrFightArea;

import com.laboschqpa.server.entity.qrfight.QrFightArea;

public class QrFightAreaDtoAdapter implements QrFightAreaJpaDto {

    private final QrFightArea delegate;

    public QrFightAreaDtoAdapter(QrFightArea delegate) {
        this.delegate = delegate;
    }

    @Override
    public Long getId() {
        return delegate.getId();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public String getDescription() {
        return delegate.getDescription();
    }

    @Override
    public Boolean getEnabled() {
        return delegate.getEnabled();
    }
}
