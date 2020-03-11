package com.laboschcst.server.api.validator;

import com.laboschcst.server.api.dto.ProfileDetailsDto;
import com.laboschcst.server.util.SelfValidator;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
public class ProfileDetailsValidator extends SelfValidator<ProfileDetailsValidator> {
    @Min(1)
    @NotNull
    private final Long userAccId;
    @Length(max = 50)
    private final String firstName;
    @Length(max = 50)
    private final String lastName;
    @Length(max = 50)
    private final String nickName;

    public ProfileDetailsValidator(ProfileDetailsDto profileDetailsDto) {
        this.userAccId = profileDetailsDto.getUserAccId();
        this.firstName = profileDetailsDto.getFirstName();
        this.lastName = profileDetailsDto.getLastName();
        this.nickName = profileDetailsDto.getNickName();

        this.validateSelf();
    }
}
