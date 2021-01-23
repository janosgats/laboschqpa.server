package com.laboschqpa.server.api.dto.ugc.submission;

import com.laboschqpa.server.util.SelfValidator;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class EditSubmissionDto extends SelfValidator {
    @NotNull
    @Min(1)
    private Long id;
    @NotNull
    @Length(max = 10000)
    private String content;

    private Set<Long> attachments;
}
