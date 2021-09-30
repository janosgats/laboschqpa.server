package com.laboschqpa.server.repo.dto.qrFightArea;

public interface QrFightAreaAndTeamSubmissionCountJpaDto {
    Long getAreaId();

    Long getTeamId();

    String getTeamName();

    Integer getSubmissionCount();
}
