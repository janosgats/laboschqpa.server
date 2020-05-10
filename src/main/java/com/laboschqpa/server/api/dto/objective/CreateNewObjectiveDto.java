package com.laboschqpa.server.api.dto.objective;

import com.laboschqpa.server.util.SelfValidator;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.time.Instant;

@Data
public class CreateNewObjectiveDto extends SelfValidator<CreateNewObjectiveDto> {
    @NotNull
    @Length(max = 10000)
    private String description;
    @NotNull
    private Boolean submittable;
    @NotNull
    private Instant deadline;
}
