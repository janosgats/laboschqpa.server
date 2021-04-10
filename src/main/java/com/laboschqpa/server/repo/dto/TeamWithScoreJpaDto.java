package com.laboschqpa.server.repo.dto;

public interface TeamWithScoreJpaDto {
    Long getId();

    String getName();

    Boolean getArchived();

    Integer getScore();
}
