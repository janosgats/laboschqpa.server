package com.laboschqpa.server.api.dto.teamscore;

import com.laboschqpa.server.util.SelfValidator;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class CreateNewTeamScoreDto extends SelfValidator {
    @NotNull
    @Min(1)
    private Long objectiveId;
    @NotNull
    @Min(1)
    private Long teamId;
    @NotNull
    private Integer score;
}
