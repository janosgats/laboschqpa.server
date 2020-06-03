package com.laboschqpa.server.api.dto.ugc.riddleeditor;

import com.laboschqpa.server.util.SelfValidator;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
public class EditRiddleDto extends SelfValidator {
    @NotNull
    @Min(1)
    private Long id;
    @NotNull
    @NotEmpty
    @Length(max = 120)
    private String title;
    @NotNull
    @NotEmpty
    @Length(max = 1000)
    private String hint;
    @NotNull
    @NotEmpty
    @Length(max = 120)
    private String solution;

    private Set<Long> attachments;
}
