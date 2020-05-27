package com.laboschqpa.server.config.filterchain.extension;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class LoginSuccessResultDto {
    private String sessionId;
    private String csrfToken;
    private Set<String> authorities;
}
