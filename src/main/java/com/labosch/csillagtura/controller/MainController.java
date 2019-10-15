package com.labosch.csillagtura.controller;

import com.labosch.csillagtura.accounts.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationProvider;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class MainController {

    @GetMapping("/test")
    public UserPrincipal getTestPage(@AuthenticationPrincipal UserPrincipal userPrincipal) {

        return userPrincipal;
    }
    @GetMapping("/")
    public RedirectView getRedir(@AuthenticationPrincipal UserPrincipal userPrincipal) {

        return new RedirectView("oauth_login");
    }

}
