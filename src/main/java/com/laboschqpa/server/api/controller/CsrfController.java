package com.laboschqpa.server.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/csrf")
public class CsrfController {
    private final CsrfTokenRepository csrfTokenRepository;

    @GetMapping("/token")
    public String getToken(HttpServletRequest httpServletRequest) {
        final CsrfToken loadedToken = csrfTokenRepository.loadToken(httpServletRequest);
        if (loadedToken == null) {
            return null;
        }
        return loadedToken.getToken();
    }
}
