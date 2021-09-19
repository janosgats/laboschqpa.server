package com.laboschqpa.server.repo.usergeneratedcontent.dto;

import com.laboschqpa.server.entity.usergeneratedcontent.Program;

public class ProgramWithTeamScoreDtoAdapter extends ProgramDtoAdapter implements GetProgramWithTeamScoreJpaDto {
    private final Integer teamScore;

    public ProgramWithTeamScoreDtoAdapter(Program delegate, Integer teamScore) {
        super(delegate);
        this.teamScore = teamScore;
    }

    @Override
    public Integer getTeamScore() {
        return teamScore;
    }
}
