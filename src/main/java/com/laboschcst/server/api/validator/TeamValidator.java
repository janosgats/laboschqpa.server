package com.laboschcst.server.api.validator;

import com.laboschcst.server.api.dto.TeamDto;
import com.laboschcst.server.util.SelfValidator;
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
