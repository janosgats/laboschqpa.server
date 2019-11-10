package com.labosch.csillagtura.controller;

import com.labosch.csillagtura.config.AppConstants;
import com.labosch.csillagtura.config.auth.user.CustomOauth2User;
import com.labosch.csillagtura.entity.UserAcc;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {

    @RequestMapping(AppConstants.loginPageUrl)
    public String getLoginPage(Model model) {
        Map<String, String> oauth2AuthenticationUrls = new HashMap<>();

        oauth2AuthenticationUrls.put("Google", AppConstants.oAuthAuthorizationRequestBaseUri + "google");
        oauth2AuthenticationUrls.put("GitHub", AppConstants.oAuthAuthorizationRequestBaseUri + "github");

        model.addAttribute("oauth2AuthenticationUrls", oauth2AuthenticationUrls);

        return "loginPage";
    }

    @GetMapping("/loginSuccess")
    public String getLogSuc(Model model) {
        SecurityContext sc = SecurityContextHolder.getContext();
        CustomOauth2User customOauth2User = ((CustomOauth2User) sc.getAuthentication().getPrincipal());
        UserAcc currentUserAcc = customOauth2User.getUserAccEntity();

        model.addAttribute("name", currentUserAcc.getId());

        return "loginSuccess";
    }

    @GetMapping("/loginFailure")
    public String getLoginFailure(Model model) {
        return "loginFailure";
    }

    @RequestMapping("/")
    public String getRootForward(Model model) {
        return "rootIndex";
    }
}
