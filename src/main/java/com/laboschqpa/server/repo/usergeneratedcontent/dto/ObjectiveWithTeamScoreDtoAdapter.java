package com.laboschqpa.server.repo.usergeneratedcontent.dto;

import com.laboschqpa.server.entity.usergeneratedcontent.Objective;

public class ObjectiveWithTeamScoreDtoAdapter extends ObjectiveDtoAdapter implements GetObjectiveWithTeamScoreJpaDto {
    private final Integer observerTeamScore;

    public ObjectiveWithTeamScoreDtoAdapter(Objective delegate, Integer observerTeamScore) {
        super(delegate);
        this.observerTeamScore = observerTeamScore;
    }

    @Override
    public Integer getObserverTeamScore() {
        return observerTeamScore;
    }
}
