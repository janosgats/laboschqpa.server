package com.laboschqpa.server.api.validator;

import com.laboschqpa.server.api.dto.team.TeamDto;
import com.laboschqpa.server.util.SelfValidator;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
public class TeamValidator extends SelfValidator<TeamValidator> {
    @Min(1)
    private final Long id;
    @Length(max = 50)
    @NotNull
    private final String name;

    public TeamValidator(TeamDto teamDto) {
        this.id = teamDto.getId();
        this.name = teamDto.getName();

        this.validateSelf();
    }
}
