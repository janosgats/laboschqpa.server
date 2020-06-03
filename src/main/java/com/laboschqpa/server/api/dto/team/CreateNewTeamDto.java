package com.laboschqpa.server.api.dto.team;

import com.laboschqpa.server.config.helper.AppConstants;
import com.laboschqpa.server.util.SelfValidator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateNewTeamDto extends SelfValidator {
    @Length(max = 50)
    @Pattern(regexp = AppConstants.generalNameValidatorPattern)
    @NotEmpty
    @NotBlank
    @NotNull
    private String name;
}
