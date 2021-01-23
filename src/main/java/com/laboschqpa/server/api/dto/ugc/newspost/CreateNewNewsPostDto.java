package com.laboschqpa.server.api.dto.ugc.newspost;

import com.laboschqpa.server.util.SelfValidator;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class CreateNewNewsPostDto extends SelfValidator {
    @NotNull
    @Length(max = 30000)
    private String content;

    private Set<Long> attachments;
}
