package com.laboschqpa.server.api.dto.ugc.submission;

import com.laboschqpa.server.util.SelfValidator;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
public class CreateNewSubmissionDto extends SelfValidator<CreateNewSubmissionDto> {
    @NotNull
    @Min(1)
    private Long objectiveId;
    @NotNull
    @Length(max = 10000)
    private String content;

    private Set<Long> attachments;
}
