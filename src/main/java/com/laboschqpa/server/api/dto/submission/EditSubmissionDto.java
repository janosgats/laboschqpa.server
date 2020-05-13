package com.laboschqpa.server.api.dto.submission;

import com.laboschqpa.server.util.SelfValidator;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class EditSubmissionDto extends SelfValidator<EditSubmissionDto> {
    @NotNull
    @Min(1)
    private Long id;
    @NotNull
    @Length(max = 10000)
    private String content;
}
