package com.laboschqpa.server.api.dto.ugc.submission;

import com.laboschqpa.server.util.SelfValidator;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class CreateNewSubmissionDto extends SelfValidator {
    @NotNull
    @Min(1)
    private Long objectiveId;
    @NotNull
    @Length(max = 20000)
    private String content;

    private Set<Long> attachments;
}
