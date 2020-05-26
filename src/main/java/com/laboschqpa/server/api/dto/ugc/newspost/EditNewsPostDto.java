package com.laboschqpa.server.api.dto.ugc.newspost;

import com.laboschqpa.server.util.SelfValidator;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
public class EditNewsPostDto extends SelfValidator<EditNewsPostDto> {
    @NotNull
    @Min(1)
    private Long id;
    @NotNull
    @Length(max = 30000)
    private String content;

    private Set<Long> attachments;
}
