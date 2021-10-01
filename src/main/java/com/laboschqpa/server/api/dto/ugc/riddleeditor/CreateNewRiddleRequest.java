package com.laboschqpa.server.api.dto.ugc.riddleeditor;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.laboschqpa.server.enums.RiddleCategory;
import com.laboschqpa.server.enums.converter.jackson.RiddleCategoryFromValueJacksonConverter;
import com.laboschqpa.server.util.SelfValidator;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class CreateNewRiddleRequest extends SelfValidator {
    @NotNull
    @NotEmpty
    @Length(max = 300)
    private String title;
    @NotNull
    @JsonDeserialize(converter = RiddleCategoryFromValueJacksonConverter.class)
    private RiddleCategory category;
    @NotNull
    @Length(max = 1000)
    private String hint;
    @NotNull
    @NotEmpty
    @Length(max = 300)
    private String solution;

    private Set<Long> attachments;
}
