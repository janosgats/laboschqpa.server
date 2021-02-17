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
    private boolean newlyRegistered;
    private Set<String> authorities;
}
