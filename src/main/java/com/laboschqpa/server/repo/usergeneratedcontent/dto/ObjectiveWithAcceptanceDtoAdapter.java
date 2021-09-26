package com.laboschqpa.server.repo.usergeneratedcontent.dto;

import com.laboschqpa.server.entity.usergeneratedcontent.Objective;

public class ObjectiveWithAcceptanceDtoAdapter extends ObjectiveDtoAdapter implements GetObjectiveWithAcceptanceJpaDto {
    private final Boolean isAccepted;

    public ObjectiveWithAcceptanceDtoAdapter(Objective delegate, Boolean isAccepted) {
        super(delegate);
        this.isAccepted = isAccepted;
    }

    @Override
    public Boolean getIsAcceptedForTeam() {
        return isAccepted;
    }
}
