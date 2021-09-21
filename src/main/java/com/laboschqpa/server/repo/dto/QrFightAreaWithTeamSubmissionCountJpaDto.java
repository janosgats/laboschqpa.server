package com.laboschqpa.server.repo.dto;

public interface QrFightAreaWithTeamSubmissionCountJpaDto {
    Long getAreaId();

    Long getTeamId();

    String getTeamName();

    Integer getSubmissionCount();
}
