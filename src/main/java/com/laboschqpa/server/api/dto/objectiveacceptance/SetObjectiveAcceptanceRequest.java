package com.laboschqpa.server.api.dto.objectiveacceptance;

import com.laboschqpa.server.util.SelfValidator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetObjectiveAcceptanceRequest extends SelfValidator {
    @NotNull
    @Min(1)
    private Long objectiveId;
    @NotNull
    @Min(1)
    private Long teamId;
    @NotNull
    private Boolean wantedIsAccepted;
}
