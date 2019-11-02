package com.labosch.csillagtura.controller;

import com.labosch.csillagtura.config.auth.AuthConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {
    @GetMapping(AuthConstants.loginPageUrl)
    public String getLoginPage(Model model) {
        Map<String, String> oauth2AuthenticationUrls = new HashMap<>();

        oauth2AuthenticationUrls.put("Google", AuthConstants.oAuthAuthorizationRequestBaseUri + "google");
        oauth2AuthenticationUrls.put("GitHub", AuthConstants.oAuthAuthorizationRequestBaseUri + "github");

        model.addAttribute("urls", oauth2AuthenticationUrls);

        return "oauth_login";
    }

    @GetMapping("/loginFailure")
    public String getLoginFailure(Model model) {
        return "loginFailure";
    }
}
