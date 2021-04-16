package com.laboschqpa.server.api.dto.ugc.objective;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.laboschqpa.server.enums.converter.jackson.ObjectiveTypeFromValueJacksonConverter;
import com.laboschqpa.server.enums.ugc.ObjectiveType;
import com.laboschqpa.server.util.SelfValidator;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class CreateNewObjectiveRequest extends SelfValidator {
    @NotNull
    @NotBlank
    @Length(max = 300)
    private String title;
    @NotNull
    @Length(max = 30000)
    private String description;
    @NotNull
    private Boolean submittable;
    @NotNull
    private Instant deadline;
    @NotNull
    @JsonDeserialize(converter = ObjectiveTypeFromValueJacksonConverter.class)
    private ObjectiveType objectiveType;

    private Set<Long> attachments;
}
