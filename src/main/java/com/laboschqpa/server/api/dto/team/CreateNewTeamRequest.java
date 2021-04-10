package com.laboschqpa.server.api.dto.team;

import com.laboschqpa.server.config.helper.AppConstants;
import com.laboschqpa.server.util.SelfValidator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateNewTeamRequest extends SelfValidator {
    @Length(min = 3, max = 50)
    @Pattern(regexp = AppConstants.generalNameValidatorPattern)
    @NotEmpty
    @NotBlank
    @NotNull
    private String name;
}
