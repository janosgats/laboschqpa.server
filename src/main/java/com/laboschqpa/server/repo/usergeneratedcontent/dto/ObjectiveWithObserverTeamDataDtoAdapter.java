package com.laboschqpa.server.repo.usergeneratedcontent.dto;

import com.laboschqpa.server.entity.usergeneratedcontent.Objective;

public class ObjectiveWithObserverTeamDataDtoAdapter extends ObjectiveDtoAdapter implements GetObjectiveWithObserverTeamDataJpaDto {
    private final Boolean isAccepted;
    private final Boolean hasSubmission;

    public ObjectiveWithObserverTeamDataDtoAdapter(Objective delegate, Boolean isAccepted, Boolean hasSubmission) {
        super(delegate);
        this.isAccepted = isAccepted;
        this.hasSubmission = hasSubmission;
    }

    @Override
    public Boolean getIsAcceptedForTeam() {
        return isAccepted;
    }

    @Override
    public Boolean getHasSubmissionFromTeam() {
        return hasSubmission;
    }
}
