package com.laboschqpa.server.repo.usergeneratedcontent.dto;

public interface GetObjectiveWithObserverTeamDataJpaDto extends GetObjectiveJpaDto {
    Boolean getIsAcceptedForTeam();

    Boolean getHasSubmissionFromTeam();
}
