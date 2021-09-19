package com.laboschqpa.server.api.dto.ugc.program;

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
public class CreateNewProgramRequest extends SelfValidator {
    @NotNull
    @NotBlank
    @Length(max = 250)
    private String title;
    @Length(max = 250)
    private String headline;
    @Length(max = 30000)
    private String description;
    @NotNull
    private Instant startTime;
    @NotNull
    private Instant endTime;

    private Set<Long> attachments;
}
