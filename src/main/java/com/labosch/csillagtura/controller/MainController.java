package com.labosch.csillagtura.controller;

import com.labosch.csillagtura.config.auth.AuthConstants;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class MainController {

    @GetMapping("/")
    public RedirectView getRedir() {

        return new RedirectView(AuthConstants.loginPageUrl);
    }

    @GetMapping("/loginSuccess")
    public Object getLogSuc() {
        SecurityContext sc = SecurityContextHolder.getContext();
        return sc.getAuthentication();
    }

}
