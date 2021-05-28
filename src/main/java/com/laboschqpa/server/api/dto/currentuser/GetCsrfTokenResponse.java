package com.laboschqpa.server.api.dto.currentuser;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.web.csrf.CsrfToken;

@Data
@AllArgsConstructor
public class GetCsrfTokenResponse {
    private String csrfToken;

    public GetCsrfTokenResponse(CsrfToken csrfToken) {
        this.csrfToken = csrfToken.getToken();
    }
}
