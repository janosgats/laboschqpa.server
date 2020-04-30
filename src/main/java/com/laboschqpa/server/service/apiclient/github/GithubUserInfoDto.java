package com.laboschqpa.server.service.apiclient.github;

import com.laboschqpa.server.util.SelfValidator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GithubUserInfoDto extends SelfValidator<GithubUserInfoDto> {
    @NotNull
    @NotBlank
    private String id;
    private String email;
}
