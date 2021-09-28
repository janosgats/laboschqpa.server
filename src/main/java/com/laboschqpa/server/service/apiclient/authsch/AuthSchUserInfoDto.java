package com.laboschqpa.server.service.apiclient.authsch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.laboschqpa.server.util.SelfValidator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthSchUserInfoDto extends SelfValidator {
    @NotNull
    @NotBlank
    @JsonProperty("internal_id")
    private String internalId;
    @JsonProperty("givenName")
    private String firstName;
    @JsonProperty("sn")
    private String lastName;
    private Map<String, String> linkedAccounts;
    @JsonProperty("mail")
    private String email;

    public String getSchAcc() {
        return linkedAccounts.get("schacc");
    }
}
