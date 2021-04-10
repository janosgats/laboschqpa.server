package com.laboschqpa.server.api.controller;

import com.laboschqpa.server.api.dto.user.UserInfoResponse;
import com.laboschqpa.server.api.service.UserService;
import com.laboschqpa.server.config.userservice.CustomOauth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/currentUser")
public class CurrentUserController {
    private final CsrfTokenRepository csrfTokenRepository;
    private final UserService userService;

    @GetMapping("/userInfo")
    public UserInfoResponse getUserInfo(@AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        return new UserInfoResponse(userService.getById(authenticationPrincipal.getUserId()));
    }

    @GetMapping("/csrfToken")
    public String getToken(HttpServletRequest httpServletRequest) {
        final CsrfToken loadedToken = csrfTokenRepository.loadToken(httpServletRequest);
        if (loadedToken == null) {
            return null;
        }
        return loadedToken.getToken();
    }
}
