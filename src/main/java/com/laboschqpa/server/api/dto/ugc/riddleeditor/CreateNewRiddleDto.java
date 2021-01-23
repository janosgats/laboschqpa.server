package com.laboschqpa.server.api.dto.ugc.riddleeditor;

import com.laboschqpa.server.util.SelfValidator;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class CreateNewRiddleDto extends SelfValidator {
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
