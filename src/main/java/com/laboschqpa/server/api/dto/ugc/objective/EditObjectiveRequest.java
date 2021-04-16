package com.laboschqpa.server.api.dto.ugc.objective;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.laboschqpa.server.enums.converter.jackson.ObjectiveTypeFromValueJacksonConverter;
import com.laboschqpa.server.enums.ugc.ObjectiveType;
import com.laboschqpa.server.util.SelfValidator;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class EditObjectiveRequest extends SelfValidator {
    @NotNull
    @Min(1)
    private Long id;
    @NotNull
    @NotBlank
    @Length(max = 300)
    private String title;
    @NotNull
    @Length(max = 10000)
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
