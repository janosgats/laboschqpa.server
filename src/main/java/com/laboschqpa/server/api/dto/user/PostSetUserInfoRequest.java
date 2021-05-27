package com.laboschqpa.server.api.dto.user;

import com.laboschqpa.server.config.helper.AppConstants;
import com.laboschqpa.server.util.SelfValidator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostSetUserInfoRequest extends SelfValidator {
    @NotNull
    @Min(0)
    private Long userId;
    @Length(max = 50)
    @Pattern(regexp = AppConstants.generalNameValidatorPattern)
    private String firstName;
    @Length(max = 50)
    @Pattern(regexp = AppConstants.generalNameValidatorPattern)
    private String lastName;
    @Length(max = 50)
    @Pattern(regexp = AppConstants.nickNameValidatorPattern)
    private String nickName;
}
