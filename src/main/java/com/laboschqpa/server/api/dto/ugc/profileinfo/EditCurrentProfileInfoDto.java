package com.laboschqpa.server.api.dto.ugc.profileinfo;

import com.laboschqpa.server.config.helper.AppConstants;
import com.laboschqpa.server.util.SelfValidator;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Pattern;

@Data
public class EditCurrentProfileInfoDto extends SelfValidator<EditCurrentProfileInfoDto> {
    @Length(max = 25)
    @Pattern(regexp = AppConstants.generalNameValidatorPattern)
    private String firstName;
    @Length(max = 25)
    @Pattern(regexp = AppConstants.generalNameValidatorPattern)
    private String lastName;
    @Length(max = 25)
    @Pattern(regexp = AppConstants.generalNameValidatorPattern)
    private String nickName;
}
