package com.laboschqpa.server.api.validator;

import com.laboschqpa.server.api.dto.team.TeamDto;
import com.laboschqpa.server.util.SelfValidator;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@NoArgsConstructor
@Data
public class TeamValidator extends SelfValidator<TeamValidator> {
    @Min(1)
    private Long id;

    @Length(max = 50)
    @Pattern(regexp = "^[a-zA-Z0-9!_áéíóöőúüűÁÉÍÓÖŐÚÜŰ -]+$")
    @NotEmpty
    @NotNull
    private String name;

    public TeamValidator(TeamDto teamDto) {
        this.id = teamDto.getId();
        this.name = teamDto.getName();
    }
}
